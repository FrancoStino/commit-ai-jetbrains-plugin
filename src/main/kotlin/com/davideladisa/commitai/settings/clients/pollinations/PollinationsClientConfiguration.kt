package com.davideladisa.commitai.settings.clients.pollinations

import com.davideladisa.commitai.Icons
import com.davideladisa.commitai.settings.clients.BaseRestLLMClientConfiguration
import com.davideladisa.commitai.settings.clients.LLMClientConfiguration
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.vcs.commit.AbstractCommitWorkflowHandler
import kotlinx.coroutines.Job
import javax.swing.Icon

class PollinationsClientConfiguration : BaseRestLLMClientConfiguration(
    DEFAULT_MODEL,
    DEFAULT_MODEL,
    "0.7"
) {

    init {
        templatePresentation.text = "${getClientName()}: $modelId"
        host = "https://text.pollinations.ai/openai"
    }

    @Attribute
    var seed: Int = (0..Int.MAX_VALUE).random()

    companion object {
        const val CLIENT_NAME = "Pollinations"
        const val DEFAULT_MODEL = "openai-large"
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
        copyFieldsTo(copy)
        copy.seed = seed
        return copy
    }

    override fun panel() = PollinationsClientPanel(this)

    override fun equals(other: Any?): Boolean {
        if (!super.equals(other)) return false
        if (other !is PollinationsClientConfiguration) return false

        return seed == other.seed
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + seed
        return result
    }
}