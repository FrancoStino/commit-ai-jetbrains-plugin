package com.davideladisa.commitai

import com.davideladisa.commitai.settings.AppSettings2
import com.davideladisa.commitai.settings.ProjectSettings
import com.davideladisa.commitai.settings.clients.LLMClientConfiguration
import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vcs.VcsDataKeys
import com.intellij.vcs.commit.AbstractCommitWorkflowHandler
import com.intellij.vcs.commit.isAmendCommitMode

class CommitAISplitButtonAction : SplitButtonAction(object : ActionGroup() {

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        // Always get fresh configurations from AppSettings2
        val configurations = AppSettings2.instance.llmClientConfigurations.toList()
            .sortedWith(
                compareBy<LLMClientConfiguration> {
                    it.id != AppSettings2.instance.activeLlmClientId
                }.thenBy {
                    it.name
                }
            )

        val actions = mutableListOf<AnAction>()

        // Use wrapper actions for all configurations to ensure fresh data
        configurations.forEach { config ->
            actions.add(LLMClientWrapperAction(config.id))
        }

        // Add separator if we have multiple items
        if (actions.size > 1) {
            actions.add(1, Separator.getInstance())
        }

        // Add separator and settings action at the end
        if (actions.isNotEmpty()) {
            actions.add(Separator.getInstance())
        }
        actions.add(SettingsAction())

        return actions.toTypedArray()
    }
}), DumbAware, Disposable {

    init {
        // Register for disposal when the action is no longer needed
        Disposer.register(ApplicationManager.getApplication(), this)
    }

    override fun dispose() {
        // Cleanup handled automatically by message bus connection
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

    override fun useDynamicSplitButton(): Boolean {
        // Disable dynamic behavior so Settings doesn't become the main action
        return false
    }

    override fun getMainAction(e: AnActionEvent): AnAction? {
        // Get the active client and return a wrapper action with its ID
        val projectSettings = e.project?.service<ProjectSettings>()
        
        // Always create fresh action from current settings
        val activeClient = projectSettings?.getActiveLLMClientConfiguration()
            ?: AppSettings2.instance.getActiveLLMClientConfiguration()

        // Return a fresh wrapper action for the active client
        return activeClient?.let { LLMClientWrapperAction(it.id, isMainAction = true) }
    }

    override fun update(e: AnActionEvent) {
        super.update(e)

        // Show the action when commit dialog is open, but enable only when there are files in staging
        val commitWorkflowHandler = e.getData(VcsDataKeys.COMMIT_WORKFLOW_HANDLER) as? AbstractCommitWorkflowHandler<*, *>
        val projectSettings = e.project?.service<ProjectSettings>()
        
        // Use only the active client from settings, ignore session selection
        val activeLlmClient = projectSettings?.getActiveLLMClientConfiguration()
            ?: AppSettings2.instance.getActiveLLMClientConfiguration()
        val hasActiveLlmClient = activeLlmClient != null
        val hasStagedFiles = commitWorkflowHandler?.ui?.getIncludedChanges()?.isNotEmpty() == true
        val isAmendMode = commitWorkflowHandler?.workflow?.commitContext?.isAmendCommitMode == true

        // Always visible when commit dialog is open and has LLM client
        e.presentation.isVisible = commitWorkflowHandler != null && hasActiveLlmClient
        // Enabled only when there are also staged files or amend mode is active
        e.presentation.isEnabled = e.presentation.isVisible && (hasStagedFiles || isAmendMode)

        // Update icon and text to match the selected LLM client
        activeLlmClient?.let { client ->
            updatePresentationWithFreshClient(e.presentation, client.id)
        }
    }

    /**
     * Updates presentation with fresh LLM client configuration data.
     * Extracted to avoid code duplication.
     */
    private fun updatePresentationWithFreshClient(presentation: Presentation, clientId: String) {
        val freshClient = getFreshLLMClient(clientId)
        freshClient?.let {
            // Show STOP icon if job is active, otherwise show client icon
            if (it.getGenerateCommitMessageJob()?.isActive == true) {
                presentation.icon = Icons.Process.STOP.getThemeBasedIcon()
            } else {
                presentation.icon = it.getClientIcon()
            }
            presentation.text = "Generate Commit Message (${it.getClientName()}: ${it.modelId})"
        }
    }
}

/**
 * Retrieves fresh LLM client configuration by ID.
 * Extracted helper function to eliminate code duplication.
 */
private fun getFreshLLMClient(clientId: String): LLMClientConfiguration? {
    return AppSettings2.instance.llmClientConfigurations.toList()
        .find { it.id == clientId }
}

/**
 * Wrapper action that always fetches fresh LLM client configuration.
 * This ensures the action always uses the latest configuration data.
 */
private class LLMClientWrapperAction(
    private val clientId: String,
    private val isMainAction: Boolean = false
) : AnAction(), DumbAware {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val clientToExecute = if (isMainAction) {
            // For main action, always get the currently active client
            val projectSettings = project.service<ProjectSettings>()
            projectSettings.getActiveLLMClientConfiguration()
                ?: AppSettings2.instance.getActiveLLMClientConfiguration()
        } else {
            // For child actions, use the client specified by clientId
            getFreshLLMClient(clientId)
        }

        clientToExecute?.execute(e)
    }

    override fun update(e: AnActionEvent) {
        // Always get fresh client configuration
        val freshClient = getFreshLLMClient(clientId)

        if (freshClient != null) {
            // Update presentation with fresh data
            e.presentation.text = "${freshClient.getClientName()}: ${freshClient.modelId}"
            // Show STOP icon if job is active, otherwise show client icon
            if (freshClient.getGenerateCommitMessageJob()?.isActive == true) {
                e.presentation.icon = Icons.Process.STOP.getThemeBasedIcon()
            } else {
                e.presentation.icon = freshClient.getClientIcon()
            }
            e.presentation.isEnabledAndVisible = true
            e.presentation.description = "Generate commit message using ${freshClient.getClientName()} - ${freshClient.modelId}"
        } else {
            e.presentation.isEnabledAndVisible = false
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

    override fun equals(other: Any?): Boolean {
        if (other !is LLMClientWrapperAction) return false
        return other.clientId == clientId && other.isMainAction == isMainAction
    }

    override fun hashCode(): Int {
        return clientId.hashCode() * 31 + isMainAction.hashCode()
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