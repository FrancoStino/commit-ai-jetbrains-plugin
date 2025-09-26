package com.davideladisa.commitai.settings.clients.groq

import com.davideladisa.commitai.settings.clients.LLMClientSharedState
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "GroqClientSharedState",
    storages = [Storage("GroqClientSharedState.xml")]
)
@Service(Service.Level.APP)
class GroqClientSharedState : PersistentStateComponent<GroqClientSharedState>, LLMClientSharedState {

    companion object {
        fun getInstance(): GroqClientSharedState {
            return ApplicationManager.getApplication().getService(GroqClientSharedState::class.java)
        }
    }

    override var hosts: MutableSet<String> = mutableSetOf(
        "https://api.groq.com/openai/v1"
    )

    override var modelIds: MutableSet<String> = mutableSetOf(
        // Recommended models for text generation (ordered by performance)
        "llama-3.3-70b-versatile",
        "deepseek-r1-distill-llama-70b",
        "llama-3.1-8b-instant",
        "groq/compound",
        "groq/compound-mini",
        "moonshotai/kimi-k2-instruct",
        "moonshotai/kimi-k2-instruct-0905",
        "qwen/qwen3-32b",
        "openai/gpt-oss-120b",
        "openai/gpt-oss-20b",
        "meta-llama/llama-4-scout-17b-16e-instruct",
        "meta-llama/llama-4-maverick-17b-128e-instruct",
        "gemma2-9b-it",
        "allam-2-7b"
    )

    override fun getState(): GroqClientSharedState = this

    override fun loadState(state: GroqClientSharedState) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
