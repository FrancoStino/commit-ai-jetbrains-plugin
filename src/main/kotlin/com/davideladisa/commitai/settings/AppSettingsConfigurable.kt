package com.davideladisa.commitai.settings

import com.davideladisa.commitai.CommitAIBundle
import com.davideladisa.commitai.CommitAIBundle.message
import com.davideladisa.commitai.CommitAIUtils
import com.davideladisa.commitai.settings.clients.LLMClientConfiguration
import com.davideladisa.commitai.settings.clients.LLMClientTable
import com.davideladisa.commitai.settings.prompts.Prompt
import com.davideladisa.commitai.settings.prompts.PromptTable
import com.intellij.openapi.components.service
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.CommonActionsPanel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.*
import kotlinx.coroutines.CoroutineScope
import java.text.Collator
import java.util.*

// Most of the settings are global, but we use project configurable to set isProjectSpecificLLMClient property
class AppSettingsConfigurable(val project: Project, cs: CoroutineScope) : BoundConfigurable(message("settings.general.group.title")) {

    private val projectSettings = project.service<ProjectSettings>()
    private val llmClientTable = LLMClientTable()
    private lateinit var llmClientConfigurationComboBox: ComboBox<LLMClientConfiguration>
    private var isProjectSpecificLLMClientCheckBox = JBCheckBox(message("settings.llmClient.projectSpecific"))
    private lateinit var llmClientToolbarDecorator: ToolbarDecorator
    private val promptTable = PromptTable(cs)
    private val isProjectSpecificPromptCheckBox = JBCheckBox(message("settings.prompt.projectSpecific"))
    private lateinit var toolbarDecorator: ToolbarDecorator
    private lateinit var promptComboBox: ComboBox<Prompt>

    override fun createPanel() = panel {

        row {
            label(message("settings.llmClient"))
                .widthGroup("labelPrompt")
            llmClientConfigurationComboBox =
                comboBox(
                    AppSettings2.instance.llmClientConfigurations.toList()
                        .sortedWith(compareBy({ it.getClientName() }, { it.name })),
                    CommitAIListCellRenderer()
                )
                    .bindItem(
                        getter = { projectSettings.getActiveLLMClientConfiguration() },
                        setter = { setActiveLLMClientConfiguration(it) })
                    .widthGroup("input")
                    .component
            cell(isProjectSpecificLLMClientCheckBox)
                .bindSelected(project.service<ProjectSettings>()::isProjectSpecificLLMClient)
            contextHelp(message("settings.llmClient.projectSpecific.contextHelp"))
        }

        row {
            checkBox(message("settings.llmClient.streamingResponse"))
                .bindSelected(AppSettings2.instance::useStreamingResponse)
            contextHelp(message("settings.llmClient.streamingResponse.contextHelp"))
        }
        row {
            llmClientToolbarDecorator = ToolbarDecorator.createDecorator(llmClientTable.table)
                .setAddAction {
                    llmClientTable.addLlmClient().let {
                        llmClientConfigurationComboBox.addItem(it)
                    }
                }
                .setEditAction {
                    llmClientTable.editLlmClient()?.let { (oldClient, newClient) ->
                        handleLLMClientEdit(oldClient, newClient)
                    }
                }
                .setRemoveAction {
                    llmClientTable.removeLlmClient()?.let {
                        llmClientConfigurationComboBox.removeItem(it)
                    }
                }
                .disableUpDownActions()

            cell(llmClientToolbarDecorator.createPanel())
                .align(Align.FILL)
        }.resizableRow()

        row {
            browserLink(message("settings.more-llm-clients"), CommitAIBundle.URL_LLM_CLIENTS_DISCUSSION.toString())
                .align(AlignX.RIGHT)
        }

        row {
            label(message("settings.locale")).widthGroup("labelPrompt")
            val locales = createSortedLocaleList()

            comboBox(locales, CommitAIListCellRenderer())
                .widthGroup("input")
                .bindItem(
                    getter = { locales.find { it.language == projectSettings.locale.language } ?: Locale.ENGLISH },
                    setter = { setActiveLocale(it) }
                )

            contextHelp(message("settings.locale.contextHelp"))
        }
        row {
            browserLink(message("settings.more-prompts"), CommitAIBundle.URL_PROMPTS_DISCUSSION.toString())
                .align(AlignX.RIGHT)
        }
        row {
            label(message("settings.prompt"))
                .widthGroup("labelPrompt")
            promptComboBox = comboBox(AppSettings2.instance.prompts.values, CommitAIListCellRenderer())
                .bindItem(getter = { projectSettings.activePrompt }, { setActivePrompt(it) })
                .widthGroup("input")
                .component
            cell(isProjectSpecificPromptCheckBox)
                .bindSelected(project.service<ProjectSettings>()::isProjectSpecificPrompt)
            contextHelp(message("settings.prompt.projectSpecific.contextHelp"))
        }
        row {
            toolbarDecorator = ToolbarDecorator.createDecorator(promptTable.table)
                .setAddAction {
                    promptTable.addPrompt().let {
                        promptComboBox.addItem(it)
                    }
                }
                .setEditAction {
                    promptTable.editPrompt()?.let { (oldPrompt, newPrompt) ->
                        handlePromptEdit(oldPrompt, newPrompt)
                    }
                }
                .setEditActionUpdater {
                    updateActionAvailability(CommonActionsPanel.Buttons.EDIT)
                    true
                }
                .setRemoveAction {
                    promptTable.removePrompt()?.let {
                        promptComboBox.removeItem(it)
                    }
                }
                .setRemoveActionUpdater {
                    updateActionAvailability(CommonActionsPanel.Buttons.REMOVE)
                    true
                }
                .disableUpDownActions()

            cell(toolbarDecorator.createPanel())
                .align(Align.FILL)
        }.resizableRow()

        row {
            browserLink(message("settings.report-bug"), CommitAIBundle.URL_BUG_REPORT.toString())
            browserLink(message("settings.github-star"), CommitAIBundle.URL_GITHUB.toString())
            browserLink(message("settings.kofi"), CommitAIBundle.URL_KOFI.toString())
            browserLink(message("settings.github-sponsors"), CommitAIBundle.URL_GITHUB_SPONSORS.toString())
        }
    }

    private fun setActiveLLMClientConfiguration(llmClientConfiguration: LLMClientConfiguration?) {
        llmClientConfiguration?.let {
            if (isProjectSpecificLLMClientCheckBox.isSelected) {
                project.service<ProjectSettings>().activeLlmClientId = it.id
            } else {
                AppSettings2.instance.activeLlmClientId = it.id
            }
        }
    }

    private fun setActivePrompt(prompt: Prompt?) {
        prompt?.let {
            if (isProjectSpecificPromptCheckBox.isSelected) {
                project.service<ProjectSettings>().activePrompt = it
            } else {
                AppSettings2.instance.activePrompt = it
            }
        }
    }

    private fun setActiveLocale(locale: Locale?) {
        locale?.let {
            if (isProjectSpecificPromptCheckBox.isSelected) {
                project.service<ProjectSettings>().locale = it
            } else {
                AppSettings2.instance.locale = it
            }
        }
    }

    private fun handleLLMClientEdit(oldClient: LLMClientConfiguration, newClient: LLMClientConfiguration) {
        val wasActive = isClientCurrentlyActive(oldClient)
        updateClientInComboBox(oldClient, newClient)
        if (wasActive) {
            selectClientAndUpdateSettings(newClient)
        }
    }

    private fun isClientCurrentlyActive(client: LLMClientConfiguration): Boolean {
        val wasSelectedInComboBox = llmClientConfigurationComboBox.selectedItem == client
        val wasActiveById = if (projectSettings.isProjectSpecificLLMClient) {
            projectSettings.activeLlmClientId == client.id
        } else {
            AppSettings2.instance.activeLlmClientId == client.id
        }
        return wasSelectedInComboBox || wasActiveById
    }

    private fun updateClientInComboBox(oldClient: LLMClientConfiguration, newClient: LLMClientConfiguration) {
        llmClientConfigurationComboBox.removeItem(oldClient)
        llmClientConfigurationComboBox.addItem(newClient)
    }

    private fun selectClientAndUpdateSettings(client: LLMClientConfiguration) {
        llmClientConfigurationComboBox.selectedItem = client
        if (projectSettings.isProjectSpecificLLMClient) {
            projectSettings.activeLlmClientId = client.id
        } else {
            AppSettings2.instance.activeLlmClientId = client.id
        }
    }

    private fun handlePromptEdit(oldPrompt: Prompt, newPrompt: Prompt) {
        val wasSelected = promptComboBox.selectedItem == oldPrompt
        promptComboBox.removeItem(oldPrompt)
        promptComboBox.addItem(newPrompt)
        if (wasSelected) {
            promptComboBox.selectedItem = newPrompt
        }
    }

    private fun createSortedLocaleList(): List<Locale> {
        val ideLocale = CommitAIUtils.getIDELocale()
        val collator = Collator.getInstance(ideLocale).apply {
            strength = Collator.TERTIARY
            decomposition = Collator.CANONICAL_DECOMPOSITION
        }

        return Locale.getAvailableLocales()
            .asSequence()
            .filter { it.language.isNotBlank() }
            .distinctBy { it.language }
            .sortedWith(compareBy(collator) { locale ->
                locale.getDisplayLanguage(ideLocale)
            })
            .toList()
    }

    private fun updateActionAvailability(action: CommonActionsPanel.Buttons) {
        val selectedRow = promptTable.table.selectedRow
        val selectedPrompt = promptTable.table.items[selectedRow]
        toolbarDecorator.actionsPanel.setEnabled(action, selectedPrompt.canBeChanged)
    }

    override fun isModified(): Boolean {
        return super.isModified() || promptTable.isModified() || llmClientTable.isModified()
    }

    override fun apply() {
        promptTable.apply()
        llmClientTable.apply()
        super.apply()

        // Refresh the combo box to reflect any changes made to LLM clients
        refreshLLMClientComboBox()
    }

    override fun reset() {
        promptTable.reset()
        llmClientTable.reset()
        super.reset()

        // Refresh the combo box to reflect reset state
        refreshLLMClientComboBox()
    }

    private fun refreshLLMClientComboBox() {
        // Clear and repopulate the combo box with current configurations
        llmClientConfigurationComboBox.removeAllItems()

        val sortedClients = AppSettings2.instance.llmClientConfigurations.toList()
            .sortedWith(compareBy({ it.getClientName() }, { it.name }))

        sortedClients.forEach { llmClientConfigurationComboBox.addItem(it) }

        // Restore the selected item based on current active ID
        val activeClient = if (projectSettings.isProjectSpecificLLMClient) {
            AppSettings2.instance.getActiveLLMClientConfiguration(projectSettings.activeLlmClientId)
        } else {
            AppSettings2.instance.getActiveLLMClientConfiguration()
        }

        activeClient?.let { active ->
            // Find the matching client in the combo box by ID
            for (i in 0 until llmClientConfigurationComboBox.itemCount) {
                val item = llmClientConfigurationComboBox.getItemAt(i)
                if (item?.id == active.id) {
                    llmClientConfigurationComboBox.selectedIndex = i
                    break
                }
            }
        }
    }

}
