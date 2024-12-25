package com.project.goldfish.di

import com.project.goldfish.network.socket.ChatSocketService
import com.project.goldfish.network.socket.ChatSocketServiceImpl
import com.project.goldfish.network.messages.MessageService
import com.project.goldfish.network.messages.MessageServiceImpl
import com.project.goldfish.network.rooms.ChatRoomService
import com.project.goldfish.network.rooms.ChatRoomServiceImpl
import com.project.goldfish.screen.ChatViewModel
import com.project.goldfish.screen.UsernameViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val sharedModule = module {
    viewModelOf(::UsernameViewModel)
    viewModelOf(::ChatViewModel)
    single {
        HttpClient(CIO) {
            install(WebSockets)
            install(ContentNegotiation) {
                json()
            }
        }
    }
    singleOf(::ChatSocketServiceImpl).bind<ChatSocketService>()
    singleOf(::MessageServiceImpl).bind<MessageService>()
    singleOf(::ChatRoomServiceImpl).bind<ChatRoomService>()
}