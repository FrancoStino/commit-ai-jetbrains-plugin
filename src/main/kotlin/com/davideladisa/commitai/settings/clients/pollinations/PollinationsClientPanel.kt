package com.davideladisa.commitai.settings.clients.pollinations

import com.davideladisa.commitai.CommitAIBundle
import com.davideladisa.commitai.CommitAIBundle.message
import com.davideladisa.commitai.emptyText
import com.davideladisa.commitai.settings.clients.LLMClientPanel
import com.davideladisa.commitai.temperatureValidNullable
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.ValidationInfoBuilder

class PollinationsClientPanel(private val clientConfiguration: PollinationsClientConfiguration) : LLMClientPanel(clientConfiguration) {
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

        PollinationsClientService.getInstance().verifyConfiguration(clientConfiguration, verifyLabel)
    }

    private fun Panel.tokenRow() {
        row {
            label(message("settings.llmClient.token"))
                .widthGroup("label")
            cell(tokenPasswordField)
                .bindText(getter = { "" }, setter = {
                    PollinationsClientService.getInstance().saveToken(clientConfiguration, it)
                })
                .emptyText(if (clientConfiguration.tokenIsStored) message("settings.llmClient.token.stored") else "Optional for ${PollinationsClientConfiguration.CLIENT_NAME}")
                .resizableColumn()
                .align(Align.FILL)
                .comment("API token is optional for some ${PollinationsClientConfiguration.CLIENT_NAME} models.", 50)
            button("Reset Token") {
                PollinationsClientService.getInstance().clearToken(clientConfiguration)
            }
                .align(AlignX.RIGHT)
        }
        row {
            browserLink("Get your token", CommitAIBundle.URL_POLLINATIONS_AUTH.toString())
                .align(AlignX.RIGHT)
        }
    }
}
