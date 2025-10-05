package com.davideladisa.commitai.settings.clients.groq

import com.davideladisa.commitai.Icons
import com.davideladisa.commitai.settings.clients.BaseRestLLMClientConfiguration
import com.davideladisa.commitai.settings.clients.LLMClientConfiguration
import com.intellij.openapi.project.Project
import com.intellij.vcs.commit.AbstractCommitWorkflowHandler
import kotlinx.coroutines.Job
import javax.swing.Icon

class GroqClientConfiguration : BaseRestLLMClientConfiguration(
    DEFAULT_MODEL,
    DEFAULT_MODEL,
    "0.7"
) {

    init {
        templatePresentation.text = "${getClientName()}: $modelId"
        host = "https://api.groq.com/openai/v1"
    }

    companion object {
        const val CLIENT_NAME = "Groq"
        const val DEFAULT_MODEL = "llama-3.3-70b-versatile"
    }

    override fun getClientName(): String {
        return CLIENT_NAME
    }

    override fun getClientIcon(): Icon {
        return Icons.GROQ.getThemeBasedIcon()
    }

    override fun getSharedState(): GroqClientSharedState {
        return GroqClientSharedState.getInstance()
    }

    override fun generateCommitMessage(commitWorkflowHandler: AbstractCommitWorkflowHandler<*, *>, project: Project) {
        return GroqClientService.getInstance().generateCommitMessage(this, commitWorkflowHandler, project)
    }

    override fun getGenerateCommitMessageJob(): Job? {
        return GroqClientService.getInstance().generateCommitMessageJob
    }

    override fun clone(): LLMClientConfiguration {
        val copy = GroqClientConfiguration()
        copyFieldsTo(copy)
        return copy
    }

    override fun panel() = GroqClientPanel(this)

    override fun equals(other: Any?): Boolean {
        if (!super.equals(other)) return false
        if (other !is GroqClientConfiguration) return false
        return true
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}