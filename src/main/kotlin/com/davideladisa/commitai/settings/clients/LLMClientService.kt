package com.davideladisa.commitai.settings.clients

import com.davideladisa.commitai.CommitAIBundle.message
import com.davideladisa.commitai.CommitAIUtils.computeDiff
import com.davideladisa.commitai.CommitAIUtils.constructPrompt
import com.davideladisa.commitai.CommitAIUtils.getCommonBranch
import com.davideladisa.commitai.settings.AppSettings2
import com.davideladisa.commitai.settings.ProjectSettings
import com.davideladisa.commitai.wrap
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.icons.AllIcons
import com.intellij.openapi.application.EDT
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.asContextElement
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.platform.ide.progress.withBackgroundProgress
import com.intellij.ui.components.JBLabel
import com.intellij.vcs.commit.AbstractCommitWorkflowHandler
import com.intellij.vcs.commit.isAmendCommitMode
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.StreamingChatModel
import dev.langchain4j.model.chat.response.ChatResponse
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler
import kotlinx.coroutines.*

abstract class LLMClientService<C : LLMClientConfiguration>(private val cs: CoroutineScope) {

    var generateCommitMessageJob : Job? = null

    abstract suspend fun buildChatModel(client: C): ChatModel

    abstract suspend fun buildStreamingChatModel(client: C): StreamingChatModel?

    fun generateCommitMessage(clientConfiguration: C, commitWorkflowHandler: AbstractCommitWorkflowHandler<*, *>, project: Project) {

        val commitContext = commitWorkflowHandler.workflow.commitContext
        val includedChanges = commitWorkflowHandler.ui.getIncludedChanges().toMutableList()

        generateCommitMessageJob = cs.launch(ModalityState.current().asContextElement()) {
            withBackgroundProgress(project, message("action.background")) {

                val diff = if (commitContext.isAmendCommitMode) {
                    try {
                        val commandLine = GeneralCommandLine("git", "show", "HEAD")
                        commandLine.setWorkDirectory(project.basePath)
                        val output = com.intellij.execution.process.ScriptRunnerUtil.getProcessOutput(commandLine)
                        output
                    } catch (_: Exception) {
                        // fallback to old behavior
                        computeDiff(includedChanges, false, project)
                    }
                } else {
                    computeDiff(includedChanges, false, project)
                }

                val branch = getCommonBranch(includedChanges, project)
                val prompt = constructPrompt(project.service<ProjectSettings>().activePrompt.content, diff, branch, commitWorkflowHandler.getCommitMessage(), project)

                makeRequest(clientConfiguration, prompt, onSuccess = {
                    withContext(Dispatchers.EDT) {
                        clientConfiguration.setCommitMessage(commitWorkflowHandler, prompt, cleanCommitMessage(it))
                    }
                    AppSettings2.instance.recordHit()
                }, onError = {
                    withContext(Dispatchers.EDT) {
                        commitWorkflowHandler.setCommitMessage(it)
                    }
                })
            }
        }
    }

    fun verifyConfiguration(client: C, label: JBLabel) {
        label.text = message("settings.verify.running")
        label.icon = AllIcons.General.InlineRefresh
        cs.launch(ModalityState.current().asContextElement()) {
            makeRequest(client, "test", onSuccess = {
                withContext(Dispatchers.EDT) {
                    label.text = message("settings.verify.valid")
                    label.icon = AllIcons.General.InspectionsOK
                }
            }, onError = {
                withContext(Dispatchers.EDT) {
                    label.text = it.wrap(60)
                    label.icon = AllIcons.General.InspectionsError
                }
            })
        }
    }

    private suspend fun makeRequestWithTryCatch(function: suspend () -> Unit, onError: suspend (r: String) -> Unit) {
        try {
            function()
        } catch (e: IllegalArgumentException) {
            onError(message("settings.verify.invalid", e.message ?: message("unknown-error")))
        } catch (e: Exception) {
            onError(e.message ?: message("unknown-error"))
            // Generic exceptions should be logged by the IDE for easier error reporting
            throw e
        }
    }

    private suspend fun makeRequest(client: C, text: String, onSuccess: suspend (r: String) -> Unit, onError: suspend (r: String) -> Unit) {
        makeRequestWithTryCatch(function = {
            if (AppSettings2.instance.useStreamingResponse) {
                buildStreamingChatModel(client)?.let { streamingChatModel ->
                    sendStreamingRequest(streamingChatModel, text, onSuccess)
                    return@makeRequestWithTryCatch
                }
            }
            sendRequest(client, text, onSuccess)
        }, onError = onError)
    }

    private suspend fun sendStreamingRequest(streamingModel: StreamingChatModel, text: String, onSuccess: suspend (r: String) -> Unit) {
        var response = ""
        val completionDeferred = CompletableDeferred<String>()

        withContext(Dispatchers.IO) {
            streamingModel.chat(
                listOf(
                    UserMessage.from(
                        "user",
                        text
                    )
                ),
                object : StreamingChatResponseHandler {
                    override fun onPartialResponse(partialResponse: String?) {
                        response += partialResponse
                        cs.launch {
                            onSuccess(response)
                        }
                    }

                    override fun onCompleteResponse(completeResponse: ChatResponse) {
                        completionDeferred.complete(completeResponse.aiMessage().text())
                    }

                    override fun onError(error: Throwable) {
                        completionDeferred.completeExceptionally(error)
                    }
                }
            )
            // This throws exception if completionDeferred.completeExceptionally(error) is called
            // which is handled by the function calling this function
            onSuccess(completionDeferred.await())
        }
    }

    private suspend fun sendRequest(client: C, text: String, onSuccess: suspend (r: String) -> Unit) {
        val model = buildChatModel(client)
        val response = withContext(Dispatchers.IO) {
            model.chat(
                listOf(
                    UserMessage.from(
                        "user",
                        text
                    )
                )
            ).aiMessage().text()
        }
        onSuccess(response)
    }

    private fun cleanCommitMessage(message: String): String {
        return message
            .replace("**", "")
            .replace("```", "")
            .replace(Regex("<think>.*?</think>", RegexOption.DOT_MATCHES_ALL), "")
            .trim()
    }
}
