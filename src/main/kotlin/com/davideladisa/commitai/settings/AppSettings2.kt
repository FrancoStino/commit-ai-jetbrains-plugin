package com.davideladisa.commitai.settings

import com.davideladisa.commitai.CommitAIUtils
import com.davideladisa.commitai.notifications.Notification
import com.davideladisa.commitai.notifications.sendNotification
import com.davideladisa.commitai.settings.clients.LLMClientConfiguration
import com.davideladisa.commitai.settings.clients.groq.GroqClientConfiguration
import com.davideladisa.commitai.settings.clients.pollinations.PollinationsClientConfiguration
import com.davideladisa.commitai.settings.prompts.DefaultPrompts
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.Converter
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.OptionTag
import com.intellij.util.xmlb.annotations.XCollection
import com.intellij.util.xmlb.annotations.XMap
import java.util.*

@State(
    name = AppSettings2.SERVICE_NAME,
    storages = [
        Storage("CommitAI2.xml")
    ]
)
@Service(Service.Level.APP)
class AppSettings2 : PersistentStateComponent<AppSettings2> {

    companion object {
        const val SERVICE_NAME = "com.davideladisa.commitai.settings.AppSettings2"
        val instance: AppSettings2
            get() = ApplicationManager.getApplication().getService(AppSettings2::class.java)
    }

    private var hits = 0
    var requestSupport = true
    var lastVersion: String? = null

    @OptionTag(converter = LocaleConverter::class)
    var locale: Locale = Locale.ENGLISH

    @XCollection(
        elementTypes = [
            PollinationsClientConfiguration::class,
            GroqClientConfiguration::class
        ],
        style = XCollection.Style.v2
    )
    var llmClientConfigurations = setOf<LLMClientConfiguration>(
        PollinationsClientConfiguration(),
        GroqClientConfiguration()
    )

    @Attribute
    var activeLlmClientId: String? = null

    @XMap
    var prompts = DefaultPrompts.toPromptsMap()
    var activePrompt = DefaultPrompts.BASIC.prompt
    var promptDialogWidth = 800
    var promptDialogHeight = 600

    var appExclusions: Set<String> = setOf()

    @Attribute
    var useStreamingResponse: Boolean = true

    override fun getState() = this

    override fun loadState(state: AppSettings2) {
        XmlSerializerUtil.copyBean(state, this)
        llmClientConfigurations.toList().forEach { it.afterSerialization() }
    }

    override fun noStateLoaded() {
        val appSettings = AppSettings.instance
        migrateSettingsFromVersion1(appSettings)
    }

    private fun migrateSettingsFromVersion1(appSettings: AppSettings) {
        hits = appSettings.hits
        locale = appSettings.locale
        lastVersion = appSettings.lastVersion
        requestSupport = appSettings.requestSupport
        prompts = appSettings.prompts
        activePrompt = appSettings.currentPrompt
        appExclusions = appSettings.appExclusions
    }



    fun recordHit() {
        hits++
        // Show support notification less frequently: at 50 uses, then every 250 uses
        if (requestSupport && (hits == 50 || hits > 50 && hits % 250 == 0)) {
            sendNotification(Notification.star())
        }
    }

    fun isPathExcluded(path: String): Boolean {
        return CommitAIUtils.matchesGlobs(path, appExclusions)
    }

    fun getActiveLLMClientConfiguration(): LLMClientConfiguration? {
        return getActiveLLMClientConfiguration(activeLlmClientId)
    }

    fun getActiveLLMClientConfiguration(activeLLMClientConfigurationId: String?): LLMClientConfiguration? {
        val validConfigurations = llmClientConfigurations.toList()
        return validConfigurations.find { it.id == activeLLMClientConfigurationId }
            ?: validConfigurations.firstOrNull()
    }

    fun setActiveLlmClient(newId: String) {
        // TODO @FrancoStino: Throw exception if llm client id is not valid
        llmClientConfigurations.filterNotNull().find { it.id == newId }?.let {
            activeLlmClientId = newId
        }
    }

    class LocaleConverter : Converter<Locale>() {
        override fun toString(value: Locale): String? {
            return value.toLanguageTag()
        }

        override fun fromString(value: String): Locale? {
            return Locale.forLanguageTag(value)
        }
    }
}
