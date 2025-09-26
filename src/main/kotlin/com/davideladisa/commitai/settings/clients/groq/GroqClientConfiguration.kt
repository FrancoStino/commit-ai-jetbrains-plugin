package com.davideladisa.commitai.settings.clients.groq

import com.davideladisa.commitai.Icons
import com.davideladisa.commitai.settings.clients.LLMClientConfiguration
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.Transient
import com.intellij.vcs.commit.AbstractCommitWorkflowHandler
import kotlinx.coroutines.Job
import javax.swing.Icon

class GroqClientConfiguration : LLMClientConfiguration(
    "llama-3.3-70b-versatile",
    "llama-3.3-70b-versatile",
    "0.7"
) {

    @Attribute
    var host: String = "https://api.groq.com/openai/v1"
    @Attribute
    var timeout: Int = 30
    @Attribute
    var tokenIsStored: Boolean = false
    @Transient
    var token: String? = null
    @Attribute
    var topP: Double? = null

    companion object {
        const val CLIENT_NAME = "Groq"
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
        copy.id = id
        copy.name = name
        copy.host = host
        copy.timeout = timeout
        copy.modelId = modelId
        copy.temperature = temperature
        copy.tokenIsStored = tokenIsStored
        copy.topP = topP
        return copy
    }

    override fun panel() = GroqClientPanel(this)
}