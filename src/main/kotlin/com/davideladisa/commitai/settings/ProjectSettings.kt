package com.davideladisa.commitai.settings

import com.davideladisa.commitai.CommitAIUtils
import com.davideladisa.commitai.settings.AppSettings2.LocaleConverter
import com.davideladisa.commitai.settings.clients.LLMClientConfiguration
import com.davideladisa.commitai.settings.prompts.DefaultPrompts
import com.davideladisa.commitai.settings.prompts.Prompt
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.OptionTag
import com.intellij.util.xmlb.annotations.Property
import java.util.*

@State(
        name = ProjectSettings.SERVICE_NAME,
        storages = [Storage("CommitAI.xml")]
)
@Service(Service.Level.PROJECT)
class ProjectSettings : PersistentStateComponent<ProjectSettings?> {

    companion object {
        const val SERVICE_NAME = "com.davideladisa.commitai.settings.ProjectSettings"
    }

    var projectExclusions: Set<String> = setOf()

    @Attribute
    var activeLlmClientId: String? = null
    @Attribute
    var isProjectSpecificLLMClient: Boolean = false

    @Transient
    var splitButtonActionSelectedLLMClientId: String? = null

    @Property
    var activePrompt = DefaultPrompts.BASIC.prompt
        get() = getActivePrompt(field)

    @OptionTag(converter = LocaleConverter::class)
    var locale: Locale = Locale.ENGLISH
        get() = getActiveLocale(field)
    @Attribute
    var isProjectSpecificPrompt: Boolean = false

    override fun getState() = this

    override fun loadState(state: ProjectSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    fun isPathExcluded(path: String): Boolean {
        return CommitAIUtils.matchesGlobs(path, projectExclusions)
    }

    fun getActiveLLMClientConfiguration(): LLMClientConfiguration? {
        return if (isProjectSpecificLLMClient) {
            AppSettings2.instance.getActiveLLMClientConfiguration(activeLlmClientId)
        } else {
            AppSettings2.instance.getActiveLLMClientConfiguration()
        }
    }

    fun getSplitButtonActionSelectedOrActiveLLMClient(): LLMClientConfiguration? {
        // First try to get the session client, then fall back to the active client
        return splitButtonActionSelectedLLMClientId?.let {
            AppSettings2.instance.getActiveLLMClientConfiguration(it)
        } ?: getActiveLLMClientConfiguration()
    }

    private fun getActivePrompt(activePrompt: Prompt): Prompt {
        return if (isProjectSpecificPrompt) {
            activePrompt
        } else {
            AppSettings2.instance.activePrompt
        }
    }

    private fun getActiveLocale(locale: Locale): Locale {
        return if (isProjectSpecificPrompt) {
            locale
        } else {
            AppSettings2.instance.locale
        }
    }
}
