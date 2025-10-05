package com.davideladisa.commitai.settings.clients

import com.davideladisa.commitai.Icons
import com.davideladisa.commitai.notifications.Notification
import com.davideladisa.commitai.notifications.sendNotification
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.VcsDataKeys
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.vcs.commit.AbstractCommitWorkflowHandler
import kotlinx.coroutines.Job
import java.util.*
import javax.swing.Icon

abstract class LLMClientConfiguration(
    @Attribute var name: String,
    @Attribute var modelId: String,
    @Attribute var temperature: String,
) : Cloneable, Comparable<LLMClientConfiguration>, AnAction() {

    @Attribute
    var id: String = UUID.randomUUID().toString()

    abstract fun getClientName(): String

    abstract fun getClientIcon(): Icon

    abstract fun getSharedState(): LLMClientSharedState

    fun getHosts(): Set<String> {
        return getSharedState().hosts
    }

    fun getModelIds(): Set<String> {
        return getSharedState().modelIds
    }

    fun addHost(host: String) {
        getSharedState().hosts.add(host)
    }

    fun addModelId(modelId: String) {
        getSharedState().modelIds.add(modelId)
    }

    open fun setCommitMessage(commitWorkflowHandler: AbstractCommitWorkflowHandler<*, *>, prompt: String, result: String) {
        commitWorkflowHandler.setCommitMessage(result)
    }

    abstract fun generateCommitMessage(commitWorkflowHandler: AbstractCommitWorkflowHandler<*, *>, project: Project)

    abstract fun getGenerateCommitMessageJob(): Job?

    public abstract override fun clone(): LLMClientConfiguration

    abstract fun panel(): LLMClientPanel

    override fun compareTo(other: LLMClientConfiguration): Int {
        return name.compareTo(other.name)
    }

    fun execute(e: AnActionEvent) {
        val project = e.project ?: return

        val generateCommitMessageJob = getGenerateCommitMessageJob()
        if (generateCommitMessageJob?.isActive == true) {
            generateCommitMessageJob.cancel()
            return
        }

        val commitWorkflowHandler = e.getData(VcsDataKeys.COMMIT_WORKFLOW_HANDLER) as AbstractCommitWorkflowHandler<*, *>?
        if (commitWorkflowHandler == null) {
            sendNotification(Notification.noCommitMessage())
            return
        }

        generateCommitMessage(commitWorkflowHandler, project)
    }

    override fun actionPerformed(e: AnActionEvent) {
        execute(e)
    }

    override fun update(e: AnActionEvent) {

        if (getGenerateCommitMessageJob()?.isActive == true) {
            e.presentation.icon = Icons.Process.STOP.getThemeBasedIcon()
        } else {
            e.presentation.icon = getClientIcon()
            // Show provider name with model in Git area dropdown
            e.presentation.text = "${getClientName()}: ${modelId}"
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

    open fun afterSerialization() {
        // Allow overriding
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LLMClientConfiguration) return false
        if (other::class != this::class) return false

        return id == other.id &&
                name == other.name &&
                modelId == other.modelId &&
                temperature == other.temperature
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + modelId.hashCode()
        result = 31 * result + temperature.hashCode()
        return result
    }
}
