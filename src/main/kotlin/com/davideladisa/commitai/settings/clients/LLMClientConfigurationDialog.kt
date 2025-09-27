package com.davideladisa.commitai.settings.clients

import com.davideladisa.commitai.settings.AppSettings2
import com.davideladisa.commitai.settings.AppSettingsConfigurable
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.dsl.builder.*
import javax.swing.JComponent

class LLMClientConfigurationDialog(private val project: Project) : DialogWrapper(project) {

    private val llmClientTable = LLMClientTable()

    init {
        title = "Configure LLM Clients"
        setOKButtonText("Apply")
        init()
    }

    override fun createCenterPanel(): JComponent = panel {
        row {
            checkBox("Use streaming response")
                .bindSelected(AppSettings2.instance::useStreamingResponse)
            contextHelp("Enable streaming responses from LLM providers for real-time feedback")
        }

        row {
            label("Available LLM Clients:")
        }

        row {
            val toolbarDecorator = ToolbarDecorator.createDecorator(llmClientTable.table)
                .setAddAction { llmClientTable.addLlmClient() }
                .setEditAction { llmClientTable.editLlmClient() }
                .setRemoveAction { llmClientTable.removeLlmClient() }
                .disableUpDownActions()

            cell(toolbarDecorator.createPanel())
                .align(Align.FILL)
        }.resizableRow()

        row {
            comment("Configure your LLM providers and models. Changes are applied immediately.")
        }

        row {
            link("Open Main Plugin Settings") {
                ShowSettingsUtil.getInstance().showSettingsDialog(project, AppSettingsConfigurable::class.java)
            }
        }
    }

    override fun doOKAction() {
        if (llmClientTable.isModified()) {
            llmClientTable.apply()

            // Force UI update by notifying that settings have changed
            // This ensures the split button reflects the new configuration
            project.messageBus.syncPublisher(com.intellij.openapi.application.ApplicationActivationListener.TOPIC)
        }
        super.doOKAction()
    }

    override fun doCancelAction() {
        llmClientTable.reset()
        super.doCancelAction()
    }
}