package com.project.goldfish.network.socket

import com.project.goldfish.data.MessageDto
import com.project.goldfish.logEvent
import com.project.goldfish.model.MessageData
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.websocket.Frame
import com.project.goldfish.serverName
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json

class ChatSocketRepositoryImpl(
    private val client: HttpClient,
): ChatSocketRepository {
    private var socket: WebSocketSession? = null

    override suspend fun initSession(chatId: String, userId: String): GFResult<Unit, DataError.Network> {
        return try {
            socket = client.webSocketSession {
                url(urlString = "ws://${serverName}:8080/chat-room?chatId=$chatId&userId=$userId")
            }
            if(socket?.isActive == true) {
                GFResult.Success(Unit)
            }
            else {
                logEvent("Couldn't establish a connection")
                GFResult.Error(DataError.Network.SERVER_ERROR)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            logEvent("error on init for some reason? ${e.message} ")
            logEvent(e.message ?: "Unknown error")

            GFResult.Error(DataError.Network.UNKNOWN)

        }
    }

    override suspend fun sendMessage(message: String) {
       try {
           socket?.send(Frame.Text(message))
       } catch (e: Exception) {
           logEvent(e.message ?: "Unknown error")

           e.printStackTrace()
       }
    }

    override fun observeMessages(): Flow<MessageData> {
        return try {
            socket?.incoming?.receiveAsFlow()?.filter { it is Frame.Text }?.map {
                val json = (it as? Frame.Text)?.readText() ?: ""
                val messageDto = Json.decodeFromString<MessageDto>(json)
                messageDto.toMessage()
            } ?: flow {  }
        } catch (e: Exception) {
            e.printStackTrace()
            flow {  }
        }
    }

    override suspend fun closeSession() {
        socket?.close()
    }
}