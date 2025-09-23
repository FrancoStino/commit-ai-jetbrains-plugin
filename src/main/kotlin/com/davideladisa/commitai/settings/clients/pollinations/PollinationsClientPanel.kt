package com.davideladisa.commitai.settings.clients.pollinations

import com.davideladisa.commitai.AICommitsBundle.message
import com.davideladisa.commitai.emptyText
import com.davideladisa.commitai.settings.clients.LLMClientPanel
import com.davideladisa.commitai.temperatureValidNullable
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.ValidationInfoBuilder
import com.davideladisa.commitai.settings.AppSettings
import com.davideladisa.commitai.AICommitsUtils.getCredentialAttributes
import com.davideladisa.commitai.notifications.Notification
import com.davideladisa.commitai.notifications.sendNotification
import com.intellij.notification.NotificationType
import java.awt.Color
import javax.swing.DefaultComboBoxModel
import javax.swing.DefaultListCellRenderer
import javax.swing.JLabel
import javax.swing.JList
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

class PollinationsClientPanel(private val clientConfiguration: PollinationsClientConfiguration) : LLMClientPanel(clientConfiguration) {
    private val tokenPasswordField = JBPasswordField()
    private val topPTextField = JBTextField()
    private val seedModels = setOf("gemini", "gemini-search", "openai-large", "openai-reasoning", "evil", "unity")

    override fun create() = panel {
        hostRow(clientConfiguration::host.toNullableProperty())
        timeoutRow(clientConfiguration::timeout)
        tokenRow()
        modelIdRow()
        temperatureRow(ValidationInfoBuilder::temperatureValidNullable)
        topPDoubleRow(topPTextField, clientConfiguration::topP.toNullableProperty())
        verifyRow()
    }.also {
        modelComboBox.renderer = object : DefaultListCellRenderer() {
            override fun getListCellRendererComponent(
                list: JList<*>?,
                value: Any?,
                index: Int,
                isSelected: Boolean,
                cellHasFocus: Boolean
            ): java.awt.Component {
                val component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus) as JLabel
                if (value is String && seedModels.contains(value) && !clientConfiguration.tokenIsStored && clientConfiguration.token.isNullOrEmpty()) {
                    component.foreground = Color.GRAY
                }
                return component
            }
        }

        val originalModel = modelComboBox.model
        modelComboBox.model = object : DefaultComboBoxModel<String>() {
            init {
                for (i in 0 until originalModel.size) {
                    addElement(originalModel.getElementAt(i))
                }
            }

            override fun setSelectedItem(anItem: Any?) {
                if (anItem is String && seedModels.contains(anItem) && !clientConfiguration.tokenIsStored && clientConfiguration.token.isNullOrEmpty()) {
                    // Select the first available model instead
                    val firstAvailable = (0 until size).map { getElementAt(it) }.firstOrNull { item ->
                        item !is String || !seedModels.contains(item) || clientConfiguration.tokenIsStored || !clientConfiguration.token.isNullOrEmpty()
                    }
                    super.setSelectedItem(firstAvailable)
                    return
                }
                super.setSelectedItem(anItem)
            }
        }
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
                .emptyText(if (clientConfiguration.tokenIsStored) message("settings.llmClient.token.stored") else "Optional for Pollinations")
                .resizableColumn()
                .align(Align.FILL)
                .comment("API token is optional for Pollinations", 50)
            button("Reset Token") {
                PollinationsClientService.getInstance().clearToken(clientConfiguration)
            }
                .align(AlignX.RIGHT)
        }
    }
}
