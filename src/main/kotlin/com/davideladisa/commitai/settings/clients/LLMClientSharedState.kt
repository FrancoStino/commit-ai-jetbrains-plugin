package com.davideladisa.commitai.settings.clients

interface LLMClientSharedState {

    val hosts: MutableSet<String>

    val modelIds: MutableSet<String>
}
