package com.davideladisa.commitai

import com.davideladisa.commitai.settings.AppSettings2
import com.davideladisa.commitai.settings.clients.LLMClientConfiguration
import com.davideladisa.commitai.settings.clients.LLMClientConfigurationDialog
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.vcs.VcsDataKeys
import com.intellij.vcs.commit.AbstractCommitWorkflowHandler
import com.intellij.util.IconUtil
import javax.swing.Icon
import java.awt.Component
import java.awt.Graphics

// Main action to generate the commit
class GenerateCommitAction : AnAction(), DumbAware {
    override fun getActionUpdateThread() = ActionUpdateThread.EDT

    override fun update(e: AnActionEvent) {
        val project = e.project
        val commitWorkflowHandler = e.getData(VcsDataKeys.COMMIT_WORKFLOW_HANDLER)
        val isDiffEmpty = project != null && CommitAIUtils.isCommitDiffEmpty(project, commitWorkflowHandler as? AbstractCommitWorkflowHandler<*, *>)

        val activeClient = try {
            AppSettings2.instance.getActiveLLMClientConfiguration()
        } catch (ex: Exception) {
            null
        }

        if (activeClient != null) {
            e.presentation.icon = GrayableIcon(activeClient.getClientIcon())
            e.presentation.text = "${activeClient.getClientName()}: ${activeClient.modelId}"
            e.presentation.description = "${activeClient.getClientName()}: ${activeClient.modelId}"
            e.presentation.isEnabled = !isDiffEmpty
        } else {
            e.presentation.text = "Configure LLM Client"
            e.presentation.isEnabled = true
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val activeClient = try {
            AppSettings2.instance.getActiveLLMClientConfiguration()
        } catch (ex: Exception) {
            null
        }

        if (activeClient == null) {
            LLMClientConfigurationDialog(project).show()
            return
        }

        val commitWorkflowHandler = e.getData(VcsDataKeys.COMMIT_WORKFLOW_HANDLER) as? AbstractCommitWorkflowHandler<*, *> ?: return
        activeClient.generateCommitMessage(commitWorkflowHandler, project)
    }
}

// Action to select an LLM from the dropdown
class LLMClientAction(configuration: LLMClientConfiguration) : AnAction() {
    private val configurationId = configuration.id

    override fun actionPerformed(e: AnActionEvent) {
        // Set this as the active client
        AppSettings2.instance.setActiveLlmClient(configurationId)
    }

    override fun update(e: AnActionEvent) {
        val latestConfiguration = AppSettings2.instance.llmClientConfigurations.find { it.id == configurationId }
        if (latestConfiguration != null) {
            e.presentation.text = "${latestConfiguration.getClientName()}: ${latestConfiguration.modelId}"
            e.presentation.icon = latestConfiguration.getClientIcon()
            e.presentation.isEnabledAndVisible = true
        } else {
            e.presentation.isEnabledAndVisible = false
        }
    }
}

// Action to open LLM configuration dialog
class ConfigureLLMAction : AnAction("Configure LLM Clients...") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        LLMClientConfigurationDialog(project).show()
    }
}

// Dropdown action group to select an LLM
class SelectLLMActionGroup : DefaultActionGroup(), DumbAware {
    private val actionCache = mutableMapOf<String, LLMClientAction>()
    private val configureAction = ConfigureLLMAction()

    init {
        templatePresentation.icon = AllIcons.General.ChevronDown
        templatePresentation.text = "Select LLM Client"
        templatePresentation.description = "Select LLM Client"
        isPopup = true
    }

    override fun getActionUpdateThread() = ActionUpdateThread.EDT

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val configurations = try {
            AppSettings2.instance.llmClientConfigurations.filterNotNull().sortedBy { it.name }
        } catch (ex: Exception) {
            emptyList()
        }

        val actions = mutableListOf<AnAction>()

        if (configurations.isNotEmpty()) {
            for (config in configurations) {
                val action = actionCache.getOrPut(config.id) { LLMClientAction(config) }
                actions.add(action)
            }
        }

        // Clean up cache from deleted configurations
        val configIds = configurations.map { it.id }.toSet()
        actionCache.keys.retainAll { it in configIds }

        if (actions.isNotEmpty()) {
            actions.add(Separator.getInstance())
        }
        actions.add(configureAction)

        return actions.toTypedArray()
    }
}


// Icon wrapper to show grayed out version when disabled
class GrayableIcon(private val originalIcon: Icon) : Icon {
    private val disabledIcon by lazy { IconUtil.desaturate(originalIcon) }

    override fun paintIcon(c: Component?, g: Graphics?, x: Int, y: Int) {
        if (c?.isEnabled == false) {
            disabledIcon.paintIcon(c, g, x, y)
        } else {
            originalIcon.paintIcon(c, g, x, y)
        }
    }

    override fun getIconWidth(): Int = originalIcon.iconWidth
    override fun getIconHeight(): Int = originalIcon.iconHeight
}

// The main action group for the split button
class CommitAISplitButtonAction : DefaultActionGroup(), DumbAware {
    private val generateCommitAction = GenerateCommitAction()
    private val selectLLMActionGroup = SelectLLMActionGroup()

    init {
        isPopup = false
    }
    override fun getActionUpdateThread() = ActionUpdateThread.EDT

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        return arrayOf(
            generateCommitAction,
            selectLLMActionGroup
        )
    }
}