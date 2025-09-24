package com.davideladisa.commitai

import com.davideladisa.commitai.settings.AppSettings2
import com.davideladisa.commitai.settings.ProjectSettings
import com.davideladisa.commitai.settings.clients.LLMClientConfiguration
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.actionSystem.DefaultActionGroup

class AICommitSplitButtonAction : SplitButtonAction(object : ActionGroup() {

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val configurations = AppSettings2.instance.llmClientConfigurations
            .filterNotNull()

        val grouped = configurations.groupBy { it.getClientName() }

        val actions = mutableListOf<AnAction>()
        for ((clientName, configs) in grouped) {
            val group = DefaultActionGroup(clientName, true)
            group.templatePresentation.icon = configs.first().getClientIcon()
            group.addAll(configs.sortedBy { it.modelId })
            actions.add(group)
        }

        // Sort groups: active client first, then alphabetically
        val activeId = AppSettings2.instance.activeLlmClientId
        val actionsSorted = actions.sortedWith(compareBy<AnAction> { action ->
            val group = action as DefaultActionGroup
            val hasActive = group.getChildActionsOrStubs().any { it is LLMClientConfiguration && it.id == activeId }
            if (hasActive) 0 else 1
        }.thenBy { (it as DefaultActionGroup).templatePresentation.text })
        actions.clear()
        actions.addAll(actionsSorted)

        return actions.toTypedArray()
    }
}), DumbAware {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val projectSettings = project.service<ProjectSettings>()
        val selectedId = projectSettings.splitButtonActionSelectedLLMClientId ?: AppSettings2.instance.activeLlmClientId
        val selectedConfig = AppSettings2.instance.llmClientConfigurations
            .find { it?.id == selectedId }
        selectedConfig?.actionPerformed(e)
    }

    override fun update(e: AnActionEvent) {
        val project = e.project ?: return
        val projectSettings = project.service<ProjectSettings>()
        val selectedId = projectSettings.splitButtonActionSelectedLLMClientId ?: AppSettings2.instance.activeLlmClientId
        val selectedConfig = AppSettings2.instance.llmClientConfigurations
            .find { it?.id == selectedId }
        if (selectedConfig != null) {
            e.presentation.icon = selectedConfig.getClientIcon()
            e.presentation.text = "${selectedConfig.getClientName()}: ${selectedConfig.modelId}"
        } else {
            e.presentation.isEnabled = false
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}
