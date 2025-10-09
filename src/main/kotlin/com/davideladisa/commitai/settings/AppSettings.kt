package com.davideladisa.commitai.settings

import com.davideladisa.commitai.settings.prompts.DefaultPrompts
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.Converter
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.OptionTag
import java.util.*


@State(
    name = AppSettings.SERVICE_NAME,
    storages = [Storage("CommitAI.xml")]
)
@Service(Service.Level.APP)
//@Deprecated("No longer used. Need for migration.")
class AppSettings : PersistentStateComponent<AppSettings> {

    var hits = 0

    @OptionTag(converter = LocaleConverter::class)
    var locale: Locale = Locale.ENGLISH

    var requestSupport = true
    var lastVersion: String? = null

    var prompts = DefaultPrompts.toPromptsMap()
    var currentPrompt = prompts["basic"] ?: DefaultPrompts.BASIC.prompt

    var appExclusions: Set<String> = setOf()

    companion object {
        const val SERVICE_NAME = "com.davideladisa.commitai.settings.AppSettings"
        val instance: AppSettings
            get() = ApplicationManager.getApplication().getService(AppSettings::class.java)
    }

    override fun getState() = null

    override fun loadState(state: AppSettings) {
        XmlSerializerUtil.copyBean(state, this)
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
