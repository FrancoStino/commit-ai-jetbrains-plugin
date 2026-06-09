package com.davideladisa.commitai.settings.clients.pollinations

import com.davideladisa.commitai.settings.clients.LLMClientSharedState
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "PollinationsClientSharedState",
    storages = [Storage("PollinationsClientSharedState.xml")]
)
@Service(Service.Level.APP)
class PollinationsClientSharedState : PersistentStateComponent<PollinationsClientSharedState>, LLMClientSharedState {

    companion object {
        fun getInstance(): PollinationsClientSharedState {
            return ApplicationManager.getApplication().getService(PollinationsClientSharedState::class.java)
        }
    }

    override var hosts: MutableSet<String> = mutableSetOf(
        "https://gen.pollinations.ai/v1"
    )

    override var modelIds: MutableSet<String> = mutableSetOf(
        "openai",
        "openai-fast",
        PollinationsClientConfiguration.DEFAULT_MODEL,
        "qwen-coder",
        "mistral",
        "openai-audio",
        "gemini",
        "gemini-fast",
        "deepseek",
        "grok",
        "gemini-search",
        "chickytutor",
        "midijourney",
        "claude-fast",
        "claude",
        "claude-large",
        "perplexity-fast",
        "perplexity-reasoning",
        "kimi",
        "gemini-large",
        "gemini-legacy",
        "nova-fast",
        "glm",
        "minimax",
        "nomnom"
    )

    override fun getState(): PollinationsClientSharedState = this

    override fun loadState(state: PollinationsClientSharedState) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
