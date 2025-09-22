package com.davideladisa.commitai.settings.clients.groq

import com.davideladisa.commitai.AICommitsUtils.getCredentialAttributes
import com.davideladisa.commitai.AICommitsUtils.retrieveToken
import com.davideladisa.commitai.notifications.Notification
import com.davideladisa.commitai.notifications.sendNotification
import com.davideladisa.commitai.settings.clients.LLMClientService
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.util.text.nullize
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.StreamingChatModel
import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.model.openai.OpenAiStreamingChatModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Duration

@Service(Service.Level.APP)
class GroqClientService(private val cs: CoroutineScope) : LLMClientService<GroqClientConfiguration>(cs) {

    companion object {
        @JvmStatic
        fun getInstance(): GroqClientService = service()
    }

    override suspend fun buildChatModel(client: GroqClientConfiguration): ChatModel {
        val token = client.token.nullize(true) ?: retrieveToken(client.id)?.toString(true)
        val builder = OpenAiChatModel.builder()
            .apiKey(token ?: "")
            .modelName(client.modelId)
            .timeout(Duration.ofSeconds(client.timeout.toLong()))
            .topP(client.topP)
            .baseUrl(client.host)

        client.temperature.takeIf { it.isNotBlank() }?.let {
            builder.temperature(it.toDouble())
        }

        return builder.build()
    }

    override suspend fun buildStreamingChatModel(client: GroqClientConfiguration): StreamingChatModel {
        val token = client.token.nullize(true) ?: retrieveToken(client.id)?.toString(true)
        val builder = OpenAiStreamingChatModel.builder()
            .apiKey(token ?: "")
            .modelName(client.modelId)
            .timeout(Duration.ofSeconds(client.timeout.toLong()))
            .topP(client.topP)
            .baseUrl(client.host)

        client.temperature.takeIf { it.isNotBlank() }?.let {
            builder.temperature(it.toDouble())
        }

        return builder.build()
    }

    fun saveToken(client: GroqClientConfiguration, token: String) {
        cs.launch(Dispatchers.Default) {
            try {
                PasswordSafe.instance.setPassword(getCredentialAttributes(client.id), token)
                client.tokenIsStored = true
            } catch (e: Exception) {
                sendNotification(Notification.unableToSaveToken(e.message))
            }
        }
    }

}