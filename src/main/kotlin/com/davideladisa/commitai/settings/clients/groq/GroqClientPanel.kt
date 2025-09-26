package com.davideladisa.commitai.settings.clients.groq

import com.davideladisa.commitai.CommitAIBundle
import com.davideladisa.commitai.CommitAIBundle.message
import com.davideladisa.commitai.emptyText
import com.davideladisa.commitai.settings.clients.LLMClientPanel
import com.davideladisa.commitai.temperatureValidNullable
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.ValidationInfoBuilder

class GroqClientPanel(private val clientConfiguration: GroqClientConfiguration) : LLMClientPanel(clientConfiguration) {
    private val tokenPasswordField = JBPasswordField()
    private val topPTextField = JBTextField()

    override fun create() = panel {
        hostRow(clientConfiguration::host.toNullableProperty())
        timeoutRow(clientConfiguration::timeout)
        tokenRow()
        modelIdRow()
        temperatureRow(ValidationInfoBuilder::temperatureValidNullable)
        topPDoubleRow(topPTextField, clientConfiguration::topP.toNullableProperty())
        verifyRow()
    }

    override fun verifyConfiguration() {
        clientConfiguration.host = hostComboBox.item
        clientConfiguration.timeout = socketTimeoutTextField.text.toInt()
        clientConfiguration.modelId = modelComboBox.item
        clientConfiguration.temperature = temperatureTextField.text
        clientConfiguration.token = String(tokenPasswordField.password)
        clientConfiguration.topP = topPTextField.text.toDoubleOrNull()

        GroqClientService.getInstance().verifyConfiguration(clientConfiguration, verifyLabel)
    }

    private fun Panel.tokenRow() {
        row {
            label(message("settings.llmClient.token"))
                .widthGroup("label")
            cell(tokenPasswordField)
                .bindText(getter = { "" }, setter = {
                    GroqClientService.getInstance().saveToken(clientConfiguration, it)
                })
                .emptyText(if (clientConfiguration.tokenIsStored) message("settings.llmClient.token.stored") else "gsk_...")
                .resizableColumn()
                .align(Align.FILL)
            button("Reset Token") {
                GroqClientService.getInstance().clearToken(clientConfiguration)
            }
                .align(AlignX.RIGHT)
        }
        row {
            browserLink("Get your token", CommitAIBundle.URL_GROQ_KEYS.toString())
                .align(AlignX.RIGHT)
        }
    }
}
