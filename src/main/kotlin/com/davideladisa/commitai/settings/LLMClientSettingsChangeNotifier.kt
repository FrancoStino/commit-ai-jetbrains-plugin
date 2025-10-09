package com.davideladisa.commitai.settings

import com.intellij.util.messages.Topic

/**
 * Topic for notifying when LLM client settings are changed.
 * This allows UI components to refresh when settings are applied.
 */
fun interface LLMClientSettingsChangeNotifier {

    companion object {
        val TOPIC = Topic.create(
            "LLMClientSettingsChanged",
            LLMClientSettingsChangeNotifier::class.java
        )
    }

    /**
     * Called when LLM client configurations have been modified and applied.
     */
    fun settingsChanged()
}
