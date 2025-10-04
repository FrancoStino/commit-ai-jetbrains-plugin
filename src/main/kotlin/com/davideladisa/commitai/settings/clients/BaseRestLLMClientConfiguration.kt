package com.davideladisa.commitai.settings.clients

import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.Transient

abstract class BaseRestLLMClientConfiguration(
    name: String,
    modelId: String,
    temperature: String,
) : LLMClientConfiguration(name, modelId, temperature) {

    @Attribute
    var host: String = ""

    @Attribute
    var timeout: Int = 30

    @Attribute
    var tokenIsStored: Boolean = false

    @Transient
    var token: String? = null

    @Attribute
    var topP: Double? = null

    override fun equals(other: Any?): Boolean {
        if (!super.equals(other)) return false
        if (other !is BaseRestLLMClientConfiguration) return false

        return host == other.host &&
                timeout == other.timeout &&
                tokenIsStored == other.tokenIsStored &&
                topP == other.topP
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + host.hashCode()
        result = 31 * result + timeout
        result = 31 * result + tokenIsStored.hashCode()
        result = 31 * result + (topP?.hashCode() ?: 0)
        return result
    }

    protected fun copyFieldsTo(target: BaseRestLLMClientConfiguration) {
        target.id = id
        target.name = name
        target.modelId = modelId
        target.temperature = temperature
        target.host = host
        target.timeout = timeout
        target.tokenIsStored = tokenIsStored
        target.topP = topP
    }
}
