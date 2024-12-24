package com.project.goldfish.network

import com.project.goldfish.data.MessageDto
import com.project.goldfish.domain.Message
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class MessageServiceImpl(
    private val client: HttpClient
): MessageService {
    override suspend fun getAllMessages(): List<Message> {
        return try {
            client.get(MessageService.Endpoints.GetAllMessages.url).body<List<MessageDto>>()
                .map { data -> data.toMessage() }
        } catch (e: Exception) {
            emptyList()
        }
    }
}