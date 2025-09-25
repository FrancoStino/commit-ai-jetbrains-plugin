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
}