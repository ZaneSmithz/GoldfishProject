package com.project.goldfish.di

import com.project.goldfish.SessionManager
import com.project.goldfish.SessionManagerImpl
import com.project.goldfish.domain.messages.GetAllMessagesUseCase
import com.project.goldfish.domain.messages.GetAllMessagesUseCaseImpl
import com.project.goldfish.domain.room.RetrieveChatRoomUseCase
import com.project.goldfish.domain.room.RetrieveChatRoomUseCaseImpl
import com.project.goldfish.domain.search.SearchUserUseCase
import com.project.goldfish.domain.search.SearchUserUseCaseImpl
import com.project.goldfish.domain.friends.AcceptFriendUseCase
import com.project.goldfish.domain.friends.AcceptFriendUseCaseImpl
import com.project.goldfish.domain.friends.AddFriendUseCase
import com.project.goldfish.domain.friends.AddFriendUseCaseImpl
import com.project.goldfish.domain.friends.RetrieveFriendsUseCase
import com.project.goldfish.domain.friends.RetrieveFriendsUseCaseImpl
import com.project.goldfish.domain.friends.RetrieveRequestedFriendsUseCase
import com.project.goldfish.domain.friends.RetrieveRequestedFriendsUseCaseImpl
import com.project.goldfish.domain.login.AddUserUseCase
import com.project.goldfish.domain.login.AddUserUseCaseImpl
import com.project.goldfish.domain.login.RetrieveUserUseCase
import com.project.goldfish.domain.login.RetrieveUserUseCaseImpl
import com.project.goldfish.domain.matches.RetrieveMatchUseCase
import com.project.goldfish.domain.matches.RetrieveMatchUseCaseImpl
import com.project.goldfish.network.auth.LoginRepository
import com.project.goldfish.network.auth.LoginRepositoryImpl
import com.project.goldfish.network.matches.MatchRepositoryImpl
import com.project.goldfish.network.friends.FriendRepository
import com.project.goldfish.network.friends.FriendRepositoryImpl
import com.project.goldfish.network.matches.MatchRepository
import com.project.goldfish.network.socket.ChatSocketRepository
import com.project.goldfish.network.socket.ChatSocketRepositoryImpl
import com.project.goldfish.network.messages.MessageRepository
import com.project.goldfish.network.messages.MessageRepositoryImpl
import com.project.goldfish.network.rooms.ChatRoomRepository
import com.project.goldfish.network.rooms.ChatRoomRepositoryImpl
import com.project.goldfish.network.search.SearchRepository
import com.project.goldfish.network.search.SearchRepositoryImpl
import com.project.goldfish.screen.chat.ChatViewModel
import com.project.goldfish.screen.lobby.LobbyViewModel
import com.project.goldfish.screen.login.LoginViewModel
import com.project.goldfish.screen.registration.RegistrationViewModel
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
    viewModelOf(::LobbyViewModel)
    viewModelOf(::ChatViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegistrationViewModel)
    single {
        HttpClient(CIO) {
            install(WebSockets)
            install(ContentNegotiation) {
                json()
            }
        }
    }
    singleOf(::LoginRepositoryImpl).bind<LoginRepository>()
    singleOf(::ChatSocketRepositoryImpl).bind<ChatSocketRepository>()
    singleOf(::MessageRepositoryImpl).bind<MessageRepository>()
    singleOf(::ChatRoomRepositoryImpl).bind<ChatRoomRepository>()
    singleOf(::SessionManagerImpl).bind<SessionManager>()
    singleOf(::FriendRepositoryImpl).bind<FriendRepository>()
    singleOf(::SearchRepositoryImpl).bind<SearchRepository>()
    singleOf(::MatchRepositoryImpl).bind<MatchRepository>()

    singleOf(::AcceptFriendUseCaseImpl).bind<AcceptFriendUseCase>()
    singleOf(::RetrieveFriendsUseCaseImpl).bind<RetrieveFriendsUseCase>()
    singleOf(::RetrieveRequestedFriendsUseCaseImpl).bind<RetrieveRequestedFriendsUseCase>()
    singleOf(::AddFriendUseCaseImpl).bind<AddFriendUseCase>()
    singleOf(::GetAllMessagesUseCaseImpl).bind<GetAllMessagesUseCase>()
    singleOf(::RetrieveChatRoomUseCaseImpl).bind<RetrieveChatRoomUseCase>()
    singleOf(::SearchUserUseCaseImpl).bind<SearchUserUseCase>()
    singleOf(::AddUserUseCaseImpl).bind<AddUserUseCase>()
    singleOf(::RetrieveUserUseCaseImpl).bind<RetrieveUserUseCase>()
    singleOf(::RetrieveMatchUseCaseImpl).bind<RetrieveMatchUseCase>()
}