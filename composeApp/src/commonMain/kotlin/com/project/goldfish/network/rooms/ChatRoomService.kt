package com.project.goldfish.network.rooms

import com.project.goldfish.data.ChatRoomDto
import com.project.goldfish.network.messages.ParticipantsRequest
import com.project.goldfish.serverName
import com.project.goldfish.util.Resource

interface ChatRoomService {
    suspend fun getOrCreateChatRoom(participantsRequest: ParticipantsRequest): Resource<ChatRoomDto>

    companion object {
        val BASE_URL = "http://$serverName:8080"
    }

    sealed class Endpoints(val url: String) {
        data object GetChatroom: Endpoints("$BASE_URL/chat-room/init")
    }
}