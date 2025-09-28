package com.davideladisa.commitai

import com.davideladisa.commitai.notifications.Notification
import com.davideladisa.commitai.notifications.sendNotification
import com.davideladisa.commitai.settings.ProjectSettings
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.vcs.VcsDataKeys
import com.intellij.vcs.commit.AbstractCommitWorkflowHandler

class CommitAIAction : AnAction(), DumbAware {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

    override fun update(e: AnActionEvent) {
        // Show the action when commit dialog is open, but enable only when there are files in staging
        val commitWorkflowHandler = e.getData(VcsDataKeys.COMMIT_WORKFLOW_HANDLER) as? AbstractCommitWorkflowHandler<*, *>
        val hasActiveLlmClient = e.project?.service<ProjectSettings>()?.getSplitButtonActionSelectedOrActiveLLMClient() != null
        val hasStagedFiles = commitWorkflowHandler?.ui?.getIncludedChanges()?.isNotEmpty() == true

        // Always visible when commit dialog is open and has LLM client
        e.presentation.isVisible = commitWorkflowHandler != null && hasActiveLlmClient
        // Enabled only when there are also staged files
        e.presentation.isEnabled = e.presentation.isVisible && hasStagedFiles
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val projectSettings = project.service<ProjectSettings>()
        val llmClient = projectSettings.getSplitButtonActionSelectedOrActiveLLMClient()

        if (llmClient == null) {
            Notification.clientNotSet()
            return
        }

        // Instead of calling actionPerformed directly (which is @ApiStatus.OverrideOnly),
        // we duplicate the logic from LLMClientConfiguration.actionPerformed()
        val generateCommitMessageJob = llmClient.getGenerateCommitMessageJob()
        if (generateCommitMessageJob?.isActive == true) {
            generateCommitMessageJob.cancel()
            return
        }

        val commitWorkflowHandler = e.getData(VcsDataKeys.COMMIT_WORKFLOW_HANDLER) as? AbstractCommitWorkflowHandler<*, *>
        if (commitWorkflowHandler == null) {
            sendNotification(Notification.noCommitMessage())
            return
        }

        // Remember which LLM client was used for the shortcut action
        projectSettings.splitButtonActionSelectedLLMClientId = llmClient.id

        llmClient.generateCommitMessage(commitWorkflowHandler, project)
    }

}
