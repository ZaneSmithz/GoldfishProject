package com.project.goldfish.network

import com.project.goldfish.domain.Message
import com.project.goldfish.serverName

interface MessageService {
    suspend fun getAllMessages(): List<Message>

    companion object {
        val BASE_URL = "https://$serverName:8080"
    }

    sealed class Endpoints(val url: String) {
        data object GetAllMessages: Endpoints("$BASE_URL/messages")
    }
}