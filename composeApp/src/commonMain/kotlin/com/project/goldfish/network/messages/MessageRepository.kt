package com.project.goldfish.network.messages

import com.project.goldfish.model.MessageData
import com.project.goldfish.serverName
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult

interface MessageRepository {
    suspend fun getAllMessages(chatRoomId: String): GFResult<List<MessageData>, DataError.Network>

    companion object {
        val BASE_URL = "http://$serverName:8080"
    }

    sealed class Endpoints(val url: String) {
        data object GetAllMessages: Endpoints("$BASE_URL/messages")
    }
}