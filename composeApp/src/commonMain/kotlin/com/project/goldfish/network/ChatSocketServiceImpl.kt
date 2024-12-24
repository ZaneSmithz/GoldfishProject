package com.project.goldfish.network

import com.project.goldfish.data.MessageDto
import com.project.goldfish.domain.Message
import com.project.goldfish.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.websocket.Frame
import com.project.goldfish.logEvent

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

class ChatSocketServiceImpl(
    private val client: HttpClient,
): ChatSocketService {
    private var socket: WebSocketSession? = null

    override suspend fun initSession(username: String): Resource<Unit> {
        return try {
            socket = client.webSocketSession {
                url(urlString = "ws://10.0.2.2:8080/chat-socket?username=$username")
            }
            if(socket?.isActive == true) {
                logEvent("ACTIVE SOCKET")
                Resource.Success(Unit)
            }
            else {
                logEvent("COULDNT ESTABLUSH CONNECTION!")

                Resource.Error("Couldn't establish a connection")
            }

        } catch (e: Exception) {
            logEvent("UNKNOWN EXCEPTION INIT SESSION")

            e.printStackTrace()
            Resource.Error(message = e.message ?: "Unknown error")
        }
    }

    override suspend fun sendMessage(message: String) {
       try {
           logEvent("TRY SEND!")
           socket?.send(Frame.Text(message))
       } catch (e: Exception) {
           logEvent("NO CONNECTION!")

           e.printStackTrace()
       }
    }

    override fun observeMessages(): Flow<Message> {
        return try {
            logEvent("OBSERVING MESSAGES")
            socket?.incoming?.receiveAsFlow()?.filter { it is Frame.Text }?.map {
                val json = (it as? Frame.Text)?.readText() ?: ""
                val messageDto = Json.decodeFromString<MessageDto>(json)
                messageDto.toMessage()
            } ?: flow {  }
        } catch (e: Exception) {
            logEvent("NO CONNECTION WHEN OBSERVING!")

            e.printStackTrace()
            flow {  }
        }
    }

    override suspend fun closeSession() {
        socket?.close()
    }
}