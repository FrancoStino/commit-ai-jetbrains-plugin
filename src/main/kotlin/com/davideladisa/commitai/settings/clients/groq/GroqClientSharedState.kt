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
        "meta-llama/llama-prompt-guard-2-86m",
        "openai/gpt-oss-20b",
        "playai-tts",
        "meta-llama/llama-4-maverick-17b-128e-instruct",
        "qwen/qwen3-32b",
        "llama-3.1-8b-instant",
        "moonshotai/kimi-k2-instruct",
        "meta-llama/llama-prompt-guard-2-22m",
        "gemma2-9b-it",
        "whisper-large-v3-turbo",
        "openai/gpt-oss-120b",
        "playai-tts-arabic",
        "meta-llama/llama-guard-4-12b",
        "moonshotai/kimi-k2-instruct-0905",
        "allam-2-7b",
        "deepseek-r1-distill-llama-70b",
        "whisper-large-v3",
        "groq/compound",
        "llama-3.3-70b-versatile",
        "groq/compound-mini",
        "meta-llama/llama-4-scout-17b-16e-instruct"
    )

    override fun getState(): GroqClientSharedState = this

    override fun loadState(state: GroqClientSharedState) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
