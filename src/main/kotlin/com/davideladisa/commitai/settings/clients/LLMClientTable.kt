package com.davideladisa.commitai.settings.clients

import com.davideladisa.commitai.CommitAIBundle.message
import com.davideladisa.commitai.createColumn
import com.davideladisa.commitai.settings.AppSettings2
import com.davideladisa.commitai.settings.clients.groq.GroqClientConfiguration
import com.davideladisa.commitai.settings.clients.pollinations.PollinationsClientConfiguration
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Splitter
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.ui.popup.ListItemDescriptorAdapter
import com.intellij.ui.JBCardLayout
import com.intellij.ui.components.JBList
import com.intellij.ui.popup.list.GroupedItemsListRenderer
import com.intellij.ui.table.TableView
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.ListTableModel
import com.intellij.util.ui.UIUtil
import java.awt.Component
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.ListSelectionModel.SINGLE_SELECTION
import javax.swing.table.DefaultTableCellRenderer

class LLMClientTable {
    private var llmClients = fetchSortedLlmClients()
    private val tableModel = createTableModel()

    val table = TableView(tableModel).apply {
        setShowColumns(true)
        setSelectionMode(SINGLE_SELECTION)

        columnModel.getColumn(0).preferredWidth = 100
        columnModel.getColumn(1).cellRenderer = IconTextCellRenderer()
        columnModel.getColumn(1).preferredWidth = 200
        columnModel.getColumn(1).maxWidth = 300

        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                if (e?.clickCount == 2) {
                    editLlmClient()
                }
            }
        })
    }

    private fun createTableModel(): ListTableModel<LLMClientConfiguration> = ListTableModel(
        arrayOf(
            createColumn(message("settings.llmClient.name")) { llmClient -> llmClient.getClientName() },
            createColumn<LLMClientConfiguration, LLMClientConfiguration>(message("settings.llmClient.modelId")) { llmClient -> llmClient },
            createColumn<LLMClientConfiguration, String>(message("settings.llmClient.temperature")) { llmClient -> llmClient.temperature }
        ),
        llmClients.toList()
    )

    private fun fetchSortedLlmClients(): List<LLMClientConfiguration> {
        val sortedWith = AppSettings2.instance.llmClientConfigurations.toList().sortedWith(
            compareBy({ if (it.getClientName() == "Pollinations") 0 else 1 }, { it.name })
        )
        return sortedWith
    }

    private fun updateLlmClients(newClients: List<LLMClientConfiguration>) {
        llmClients =
            newClients.sortedWith(compareBy({ if (it.getClientName() == "Pollinations") 0 else 1 }, { it.name }))
        refreshTableModel()
    }

    fun addLlmClient(): LLMClientConfiguration? {
        val dialog = LLMClientDialog()
        if (dialog.showAndGet()) {
            updateLlmClients(llmClients + dialog.llmClient)
            return dialog.llmClient
        }
        return null
    }

    fun removeLlmClient(): LLMClientConfiguration? {
        val selectedLlmClient = table.selectedObject ?: return null
        updateLlmClients(llmClients - selectedLlmClient)
        return selectedLlmClient
    }

    fun editLlmClient(): Pair<LLMClientConfiguration, LLMClientConfiguration>? {
        val selectedLlmClient = table.selectedObject ?: return null
        val clonedClient = selectedLlmClient.clone()
        val dialog = LLMClientDialog(clonedClient)
        if (dialog.showAndGet()) {
            // The cloned client preserves the original ID, which is correct
            // We need to ensure all references are updated in AppSettingsConfigurable
            updateLlmClients(llmClients - selectedLlmClient + dialog.llmClient)
            return selectedLlmClient to dialog.llmClient
        }
        return null
    }

    private fun refreshTableModel() {
        tableModel.items = llmClients.toList()
    }

    fun reset() {
        llmClients = fetchSortedLlmClients()
        refreshTableModel()
    }

    fun isModified() = llmClients.toSet() != AppSettings2.instance.llmClientConfigurations

    fun apply() {
        AppSettings2.instance.llmClientConfigurations = llmClients.toSet()
    }

    private class LLMClientDialog(val newLlmClientConfiguration: LLMClientConfiguration? = null) : DialogWrapper(true) {

        private val llmClientConfigurations: List<LLMClientConfiguration> = getLlmClients(newLlmClientConfiguration)
        var llmClient = newLlmClientConfiguration ?: llmClientConfigurations[0]

        private val cardLayout = JBCardLayout()
        private var editPanel: DialogPanel? = null

        init {
            title = newLlmClientConfiguration?.let { "Edit LLM Client" } ?: "Add LLM Client"
            setOKButtonText(newLlmClientConfiguration?.let { message("actions.update") } ?: message("actions.add"))
            init()
        }

        override fun doOKAction() {
            // Always apply changes from the panel before closing
            val activePanel = if (newLlmClientConfiguration == null) {
                cardLayout.findComponentById(llmClient.getClientName()) as DialogPanel
            } else {
                // In edit mode, use the stored panel reference
                editPanel
            }
            activePanel?.apply()
            super.doOKAction()
        }

        override fun createCenterPanel() = if (newLlmClientConfiguration == null) {
            createCardSplitter()
        } else {
            llmClient.panel().create().also { editPanel = it }
        }.apply {
            isResizable = true
            // Increased size for better usability and space for verification messages
            preferredSize = Dimension(800, 600)
            minimumSize = Dimension(700, 500)
        }

        private fun getLlmClients(newLLMClientConfiguration: LLMClientConfiguration?): List<LLMClientConfiguration> {
            return if (newLLMClientConfiguration == null) {
                // TODO(@FrancoStino): Is there a better way to create the list of all possible LLM Clients that implement LLMClient abstract class
                listOf(
                    PollinationsClientConfiguration(),
                    GroqClientConfiguration()
                ).sortedBy { if (it.getClientName() == "Pollinations") 0 else 1 }
            } else {
                listOf(newLLMClientConfiguration)
            }
        }

        private fun createCardSplitter(): JComponent {
            return Splitter(false, 0.25f).apply {

                val cardPanel = JPanel(cardLayout).apply {
                    llmClientConfigurations.forEach {
                        add(it.getClientName(), it.panel().create())
                    }
                }

                val cardsList = JBList(llmClientConfigurations).apply {
                    // Set darker background for better separation
                    background = UIUtil.getPanelBackground().darker()
                    val descriptor = object : ListItemDescriptorAdapter<LLMClientConfiguration>() {
                        override fun getTextFor(value: LLMClientConfiguration) = when {
                            value.getClientName() == "Pollinations" && newLlmClientConfiguration == null -> "${value.getClientName()} (Free)"
                            value.getClientName() == "Groq" && newLlmClientConfiguration == null -> "${value.getClientName()} (Freemium)"
                            else -> value.getClientName()
                        }
                        override fun getIconFor(value: LLMClientConfiguration) = value.getClientIcon()
                    }
                    cellRenderer = object : GroupedItemsListRenderer<LLMClientConfiguration>(descriptor) {
                        override fun customizeComponent(list: JList<out LLMClientConfiguration>?, value: LLMClientConfiguration?, isSelected: Boolean) {
                            myTextLabel.border = JBUI.Borders.empty(4)
                        }
                    }
                    addListSelectionListener {
                        llmClient = selectedValue
                        cardLayout.show(cardPanel, llmClient.getClientName())

                        // Register validators of the currently active cards
                        val dialogPanel = cardLayout.findComponentById(llmClient.getClientName()) as DialogPanel
                        dialogPanel.registerValidators(myDisposable) {
                            isOKActionEnabled = ContainerUtil.and(it.values) { info: ValidationInfo -> info.okEnabled }
                        }
                    }
                    setSelectedValue(llmClient, true)
                }

                firstComponent = cardsList
                secondComponent = cardPanel
            }
        }
    }

    class IconTextCellRenderer : DefaultTableCellRenderer() {
        override fun getTableCellRendererComponent(table: JTable, value: Any, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int): Component {
            val llmClientConfiguration = value as LLMClientConfiguration
            return JLabel(llmClientConfiguration.modelId, llmClientConfiguration.getClientIcon(), LEFT)
        }
    }

}
