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
        "https://text.pollinations.ai/openai"
    )

    override var modelIds: MutableSet<String> = mutableSetOf(
        "gemini",
        "gemini-search",
        "openai",
        "openai-fast",
        "openai-large",
        "openai-reasoning",
        "bidara",
        "evil",
        "unity"
    )

    override fun getState(): PollinationsClientSharedState = this

    override fun loadState(state: PollinationsClientSharedState) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
