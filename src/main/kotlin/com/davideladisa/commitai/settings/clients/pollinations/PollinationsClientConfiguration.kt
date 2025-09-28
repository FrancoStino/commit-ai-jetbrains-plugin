package com.davideladisa.commitai.settings.clients.pollinations

import com.davideladisa.commitai.Icons
import com.davideladisa.commitai.settings.clients.LLMClientConfiguration
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.Transient
import com.intellij.vcs.commit.AbstractCommitWorkflowHandler
import kotlinx.coroutines.Job
import javax.swing.Icon

class PollinationsClientConfiguration : LLMClientConfiguration(
    "openai-large",
    "openai-large",
    "0.7"
) {

    init {
        templatePresentation.text = "${getClientName()}: $modelId"
    }

    @Attribute
    var host: String = "https://text.pollinations.ai/openai"
    @Attribute
    var timeout: Int = 30
    @Attribute
    var tokenIsStored: Boolean = false
    @Transient
    var token: String? = null
    @Attribute
    var topP: Double? = null
    @Attribute
    var seed: Int = (0..Int.MAX_VALUE).random()

    companion object {
        const val CLIENT_NAME = "Pollinations"
    }

    override fun getClientName(): String {
        return CLIENT_NAME
    }

    override fun getClientIcon(): Icon {
        return Icons.POLLINATIONS.getThemeBasedIcon()
    }

    override fun getSharedState(): PollinationsClientSharedState {
        return PollinationsClientSharedState.getInstance()
    }

    override fun generateCommitMessage(commitWorkflowHandler: AbstractCommitWorkflowHandler<*, *>, project: Project) {
        return PollinationsClientService.getInstance().generateCommitMessage(this, commitWorkflowHandler, project)
    }

    override fun getGenerateCommitMessageJob(): Job? {
        return PollinationsClientService.getInstance().generateCommitMessageJob
    }

    override fun clone(): LLMClientConfiguration {
        val copy = PollinationsClientConfiguration()
        copy.id = id
        copy.name = name
        copy.host = host
        copy.timeout = timeout
        copy.modelId = modelId
        copy.temperature = temperature
        copy.tokenIsStored = tokenIsStored
        copy.topP = topP
        copy.seed = seed
        return copy
    }

    override fun panel() = PollinationsClientPanel(this)
}
