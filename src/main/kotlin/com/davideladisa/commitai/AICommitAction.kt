package com.davideladisa.commitai

import com.davideladisa.commitai.notifications.Notification
import com.davideladisa.commitai.notifications.sendNotification
import com.davideladisa.commitai.settings.AppSettings2
import com.davideladisa.commitai.settings.LLMClientSettingsChangeNotifier
import com.davideladisa.commitai.settings.ProjectSettings
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vcs.VcsDataKeys
import com.intellij.vcs.commit.AbstractCommitWorkflowHandler

class CommitAIAction : AnAction(), DumbAware, Disposable {

    init {
        // Listen for LLM client settings changes to force update
        val connection = ApplicationManager.getApplication().messageBus.connect(this)
        connection.subscribe(LLMClientSettingsChangeNotifier.TOPIC, object : LLMClientSettingsChangeNotifier {
            override fun settingsChanged() {
                // Force recreation of the action presentation
                ApplicationManager.getApplication().invokeLater {
                    templatePresentation.text = null
                    templatePresentation.icon = null
                }
            }
        })

        // Register for disposal when the action is no longer needed
        Disposer.register(ApplicationManager.getApplication(), this)
    }

    override fun dispose() {
        // Cleanup handled automatically by message bus connection
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

    override fun update(e: AnActionEvent) {
        // Show the action when commit dialog is open, but enable only when there are files in staging
        val commitWorkflowHandler = e.getData(VcsDataKeys.COMMIT_WORKFLOW_HANDLER) as? AbstractCommitWorkflowHandler<*, *>
        val activeLlmClient = getActiveLlmClient(e.project)
        val hasActiveLlmClient = activeLlmClient != null
        val hasStagedFiles = commitWorkflowHandler?.ui?.getIncludedChanges()?.isNotEmpty() == true

        // Always visible when commit dialog is open and has LLM client
        e.presentation.isVisible = commitWorkflowHandler != null && hasActiveLlmClient
        // Enabled only when there are also staged files
        e.presentation.isEnabled = e.presentation.isVisible && hasStagedFiles

        // Update icon to match the selected LLM client
        activeLlmClient?.let {
            // Get fresh configuration to ensure we have the latest data
            val freshClient = AppSettings2.instance.llmClientConfigurations.toList()
                .find { config -> config.id == it.id } ?: it

            // Show STOP icon if job is active, otherwise show client icon
            if (freshClient.getGenerateCommitMessageJob()?.isActive == true) {
                e.presentation.icon = Icons.Process.STOP.getThemeBasedIcon()
            } else {
                e.presentation.icon = freshClient.getClientIcon()
            }
            e.presentation.text = "Generate Commit Message (${freshClient.getClientName()}: ${freshClient.modelId})"
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val llmClient = getActiveLlmClient(project)

        if (llmClient == null) {
            Notification.clientNotSet()
            return
        }

        // Remember which LLM client was used for the shortcut action
        val projectSettings = project.service<ProjectSettings>()
        projectSettings.splitButtonActionSelectedLLMClientId = llmClient.id

        // Execute the action on the LLM client
        llmClient.execute(e)
    }

    private fun getActiveLlmClient(project: Project?) =
        project?.service<ProjectSettings>()?.getSplitButtonActionSelectedOrActiveLLMClient()

}