package com.project.goldfish.network.rooms

import com.project.goldfish.data.ChatRoomDto
import com.project.goldfish.logEvent
import com.project.goldfish.network.messages.ParticipantsRequest
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ChatRoomRepositoryImpl(
    private val client: HttpClient
): ChatRoomRepository {
    override suspend fun getOrCreateChatRoom(participantsRequest: ParticipantsRequest): GFResult<ChatRoomDto, DataError.Network> {
        logEvent("PARTIPANT REQUEST = $participantsRequest")
        return try {
            GFResult.Success(client.post(ChatRoomRepository.Endpoints.GetChatroom.url) {
                contentType(ContentType.Application.Json)
                setBody(participantsRequest)
            }.body<ChatRoomDto>())
        } catch (e: Exception) {
            e.printStackTrace()
            GFResult.Error(DataError.Network.UNKNOWN)
        }
    }
}