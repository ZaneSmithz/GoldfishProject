package com.project.goldfish.network.socket

import com.project.goldfish.model.MessageData
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult
import kotlinx.coroutines.flow.Flow

interface ChatSocketRepository {
    suspend fun initSession(chatId: String, userId: String): GFResult<Unit, DataError.Network>

    suspend fun sendMessage(message: String)

    fun observeMessages(): Flow<MessageData>

    suspend fun closeSession()


    companion object {
        const val BASE_URL = "ws://0.0.0.0:8080"
    }

    sealed class Endpoints(val url: String) {
        data object ChatSocket: Endpoints(url ="$BASE_URL/chat-socket")
    }
}