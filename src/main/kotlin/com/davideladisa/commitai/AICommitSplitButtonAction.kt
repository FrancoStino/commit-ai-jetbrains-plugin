package com.davideladisa.commitai

import com.davideladisa.commitai.settings.AppSettings2
import com.davideladisa.commitai.settings.ProjectSettings
import com.davideladisa.commitai.settings.clients.LLMClientConfiguration
import com.intellij.icons.AllIcons
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

        // Add separator and settings action at the end
        if (actions.isNotEmpty()) {
            actions.add(Separator.getInstance())
        }
        actions.add(SettingsAction())

        return actions.toTypedArray()
    }
}), DumbAware {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

    override fun useDynamicSplitButton(): Boolean {
        // Disable dynamic behavior so Settings doesn't become the main action
        return false
    }

    override fun getMainAction(e: AnActionEvent): AnAction? {
        // Always use the first enabled LLM client action, never Settings
        val session = e.updateSession
        val children = session.children(delegate)
        val firstEnabled = children.find { action ->
            val presentation = session.presentation(action)
            presentation.isEnabled && action !is SettingsAction
        }
        return firstEnabled ?: children.firstOrNull { action ->
            action !is SettingsAction
        }
    }

    override fun update(e: AnActionEvent) {
        super.update(e)

        // Show the action when commit dialog is open, but enable only when there are files in staging
        val commitWorkflowHandler = e.getData(VcsDataKeys.COMMIT_WORKFLOW_HANDLER) as? AbstractCommitWorkflowHandler<*, *>
        val hasActiveLlmClient = e.project?.service<ProjectSettings>()?.getSplitButtonActionSelectedOrActiveLLMClient() != null
        val hasStagedFiles = commitWorkflowHandler?.ui?.getIncludedChanges()?.isNotEmpty() == true

        // Always visible when commit dialog is open and has LLM client
        e.presentation.isVisible = commitWorkflowHandler != null && hasActiveLlmClient
        // Enabled only when there are also staged files
        e.presentation.isEnabled = e.presentation.isVisible && hasStagedFiles
    }
}

/**
 * Settings action that appears at the bottom of the split button dropdown.
 * This action doesn't become the main button when clicked.
 */
private class SettingsAction : AnAction() {

    init {
        templatePresentation.text = "Settings"
        templatePresentation.description = "Open Commit AI settings"
        templatePresentation.icon = AllIcons.General.Settings
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        CommitAIBundle.openPluginSettings(project)
        // Note: This action is designed not to become the main split button action
    }

    override fun update(e: AnActionEvent) {
        // Always enabled when project is available
        e.presentation.isEnabledAndVisible = e.project != null
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}