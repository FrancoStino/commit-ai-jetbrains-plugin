package com.davideladisa.commitai

import com.davideladisa.commitai.settings.AppSettings2
import com.davideladisa.commitai.settings.LLMClientSettingsChangeNotifier
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

class CommitAISplitButtonAction : SplitButtonAction(object : ActionGroup() {

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        // Always get fresh configurations from AppSettings2
        val configurations = AppSettings2.instance.llmClientConfigurations
            .filterNotNull()
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
        // Listen for LLM client settings changes to force update
        val connection = ApplicationManager.getApplication().messageBus.connect(this)
        connection.subscribe(LLMClientSettingsChangeNotifier.TOPIC, object : LLMClientSettingsChangeNotifier {
            override fun settingsChanged() {
                // Force complete recreation of the split button
                ApplicationManager.getApplication().invokeLater {
                    // Clear template presentation to force refresh
                    templatePresentation.text = null
                    templatePresentation.icon = null
                    templatePresentation.description = null

                    // Force the action manager to update this action
                    ActionManager.getInstance().getAction("CommitAISplitButton")?.let { action ->
                        if (action is CommitAISplitButtonAction) {
                            // Clear any cached state to force refresh on next update
                            action.templatePresentation.isEnabledAndVisible = true
                        }
                    }
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

    override fun useDynamicSplitButton(): Boolean {
        // Disable dynamic behavior so Settings doesn't become the main action
        return false
    }

    override fun getMainAction(e: AnActionEvent): AnAction? {
        // Get the active client and return a wrapper action with its ID
        val projectSettings = e.project?.service<ProjectSettings>()
        val activeClient = projectSettings?.getSplitButtonActionSelectedOrActiveLLMClient()
            ?: AppSettings2.instance.getActiveLLMClientConfiguration()

        // Return a fresh wrapper action for the active client
        return activeClient?.let { LLMClientWrapperAction(it.id) }
    }

    override fun update(e: AnActionEvent) {
        super.update(e)

        // Show the action when commit dialog is open, but enable only when there are files in staging
        val commitWorkflowHandler = e.getData(VcsDataKeys.COMMIT_WORKFLOW_HANDLER) as? AbstractCommitWorkflowHandler<*, *>
        val projectSettings = e.project?.service<ProjectSettings>()
        val activeLlmClient = projectSettings?.getSplitButtonActionSelectedOrActiveLLMClient()
        val hasActiveLlmClient = activeLlmClient != null
        val hasStagedFiles = commitWorkflowHandler?.ui?.getIncludedChanges()?.isNotEmpty() == true

        // Always visible when commit dialog is open and has LLM client
        e.presentation.isVisible = commitWorkflowHandler != null && hasActiveLlmClient
        // Enabled only when there are also staged files
        e.presentation.isEnabled = e.presentation.isVisible && hasStagedFiles

        // Update icon and text to match the selected LLM client
        activeLlmClient?.let {
            // Get fresh configuration to ensure we have the latest data
            val freshClient = AppSettings2.instance.llmClientConfigurations
                .filterNotNull()
                .find { config -> config.id == it.id } ?: it

            e.presentation.icon = freshClient.getClientIcon()
            e.presentation.text = "Generate Commit Message (${freshClient.getClientName()}: ${freshClient.modelId})"
        }
    }
}

/**
 * Wrapper action that always fetches fresh LLM client configuration.
 * This ensures the action always uses the latest configuration data.
 */
private class LLMClientWrapperAction(private val clientId: String) : AnAction(), DumbAware {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        // Get fresh configuration
        val freshClient = AppSettings2.instance.llmClientConfigurations
            .filterNotNull()
            .find { it.id == clientId }

        if (freshClient != null) {
            // Store this selection for the session
            val projectSettings = project.service<ProjectSettings>()
            projectSettings.splitButtonActionSelectedLLMClientId = clientId

            // Execute with fresh client
            freshClient.execute(e)
        }
    }

    override fun update(e: AnActionEvent) {
        // Always get fresh client configuration
        val freshClient = AppSettings2.instance.llmClientConfigurations
            .filterNotNull()
            .find { it.id == clientId }

        if (freshClient != null) {
            // Update presentation with fresh data
            e.presentation.text = "${freshClient.getClientName()}: ${freshClient.modelId}"
            e.presentation.icon = freshClient.getClientIcon()
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
        return other is LLMClientWrapperAction && other.clientId == clientId
    }

    override fun hashCode(): Int {
        return clientId.hashCode()
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
