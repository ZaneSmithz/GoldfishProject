package com.project.goldfish.network.messages

import com.project.goldfish.data.MessageDto
import com.project.goldfish.domain.Message
import com.project.goldfish.logEvent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
data class ParticipantsRequest(val participants: List<String>)

@Serializable
data class ChatRoomMessageRequest(val chatId: String)

class MessageServiceImpl(
    private val client: HttpClient
): MessageService {
    override suspend fun getAllMessages(chatRoomId: String): List<Message> {
        return try {
            client.post(MessageService.Endpoints.GetAllMessages.url) {
                contentType(ContentType.Application.Json)
                setBody(ChatRoomMessageRequest(chatRoomId))
            }.body<List<MessageDto>>().map { data ->
                logEvent("Message: $data")
                data.toMessage()
            }
        } catch (e: Exception) {
            logEvent("${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}