package com.project.goldfish.network

import com.project.goldfish.domain.Message

interface MessageService {
    suspend fun getAllMessages(): List<Message>

    companion object {
        const val BASE_URL = "https://10.0.2.2:8080"
    }

    sealed class Endpoints(val url: String) {
        data object GetAllMessages: Endpoints("$BASE_URL/messages")
    }
}