package com.davideladisa.commitai

import com.davideladisa.commitai.settings.AppSettings2
import com.davideladisa.commitai.settings.ProjectSettings
import com.davideladisa.commitai.settings.clients.LLMClientConfiguration
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.vcs.VcsDataKeys
import com.intellij.vcs.commit.AbstractCommitWorkflowHandler

class CommitAISplitButtonAction : SplitButtonAction(object : ActionGroup() {

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val configurations = AppSettings2.instance.llmClientConfigurations.sortedWith(
            compareBy<LLMClientConfiguration> {
                it.id != AppSettings2.instance.activeLlmClientId
            }.thenBy {
                it.name
            }
        )

        val actions = mutableListOf<AnAction>()
        if (configurations.isNotEmpty()) {
            actions.add(configurations.first())

            if (configurations.size > 1) {
                actions.add(Separator.getInstance())
            }

            actions.addAll(configurations.drop(1))
        }

        return actions.toTypedArray()
    }
}), DumbAware {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        
        // Set the icon to the active LLM client's icon
        val project = e.project
        if (project != null) {
            val projectSettings = project.service<ProjectSettings>()
            val activeLlmClient = projectSettings.getSplitButtonActionSelectedOrActiveLLMClient()
            if (activeLlmClient != null) {
                e.presentation.icon = activeLlmClient.getClientIcon()
                e.presentation.text = activeLlmClient.name
            }
        }
        
        // Check if diff is empty and disable if so
        val commitWorkflowHandler = e.getData(VcsDataKeys.COMMIT_WORKFLOW_HANDLER)
        val isDiffEmpty = project != null && CommitAIUtils.isCommitDiffEmpty(project, commitWorkflowHandler as? AbstractCommitWorkflowHandler<*, *>)
        
        e.presentation.isEnabled = e.presentation.isEnabled && !isDiffEmpty
    }
}