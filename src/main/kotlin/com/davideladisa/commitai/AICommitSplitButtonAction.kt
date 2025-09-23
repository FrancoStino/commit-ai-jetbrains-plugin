package com.davideladisa.commitai

import com.davideladisa.commitai.settings.AppSettings2
import com.davideladisa.commitai.settings.clients.LLMClientConfiguration
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.DumbAware

class AICommitSplitButtonAction : SplitButtonAction(object : ActionGroup() {

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
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
        if (configurations.isNotEmpty()) {
            actions.add(configurations.first())
            actions.addAll(configurations.drop(1))
        }

        return actions.toTypedArray()
    }
}), DumbAware {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}
