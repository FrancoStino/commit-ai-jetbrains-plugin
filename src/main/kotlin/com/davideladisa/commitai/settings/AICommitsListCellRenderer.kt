package com.davideladisa.commitai.settings

import com.davideladisa.commitai.AICommitsUtils
import com.davideladisa.commitai.settings.clients.LLMClientConfiguration
import com.davideladisa.commitai.settings.prompts.Prompt
import java.awt.Component
import java.util.*
import javax.swing.DefaultListCellRenderer
import javax.swing.JList

class AICommitsListCellRenderer : DefaultListCellRenderer() {
    override fun getListCellRendererComponent(
        list: JList<*>?,
        value: Any?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        val component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
        when (value) {
            is Locale -> {
                val ideLocale = AICommitsUtils.getIDELocale()
                // lang format: "Language name in IDE's locale + (ISO 639 lang code)"; example for Spanish in IDE "francés (fr)", for IDE in German "Französisch (fr)"
                text = "${Locale.forLanguageTag(value.language).getDisplayLanguage(ideLocale)} (${value.language})"
            }

            is Prompt -> {
                text = value.name
            }

            // This is used for combo box in settings dialog
            is LLMClientConfiguration -> {
                text = "${value.getClientName()}: ${value.modelId}"
                icon = value.getClientIcon()
            }
        }
        return component
    }
}
