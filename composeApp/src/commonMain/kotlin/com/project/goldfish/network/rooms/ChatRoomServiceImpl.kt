package com.project.goldfish.network.rooms

import com.project.goldfish.data.ChatRoomDto
import com.project.goldfish.network.messages.ParticipantsRequest
import com.project.goldfish.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ChatRoomServiceImpl(
    private val client: HttpClient
): ChatRoomService {
    override suspend fun getOrCreateChatRoom(participantsRequest: ParticipantsRequest): Resource<ChatRoomDto> {
        return try {
            Resource.Success(client.post(ChatRoomService.Endpoints.GetChatroom.url) {
                contentType(ContentType.Application.Json)
                setBody(participantsRequest)
            }.body<ChatRoomDto>())
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(message = e.message ?: "Unknown error")
        }
    }
}