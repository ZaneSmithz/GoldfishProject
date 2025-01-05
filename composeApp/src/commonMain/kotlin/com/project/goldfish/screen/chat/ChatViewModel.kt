package com.project.goldfish.screen.chat

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.goldfish.SessionManager
import com.project.goldfish.model.state.ChatState
import com.project.goldfish.logEvent
import com.project.goldfish.model.event.ChatEvent
import com.project.goldfish.network.socket.ChatSocketRepository
import com.project.goldfish.network.messages.MessageRepository
import com.project.goldfish.network.messages.ParticipantsRequest
import com.project.goldfish.network.rooms.ChatRoomRepository
import com.project.goldfish.util.GFResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class ChatViewModel(
    private val messageService: MessageRepository,
    private val chatSocketService: ChatSocketRepository,
    private val chatRoomService: ChatRoomRepository,
    private val sessionManager: SessionManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private var _messageText = mutableStateOf("")
    val messageText: State<String> = _messageText

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    fun onEvent(chatEvent: ChatEvent) {
        when (chatEvent) {
            is ChatEvent.OnConnect -> {
                _state.value = state.value.copy(
                    isLoading = true,
                    username = sessionManager.state.value.user?.username ?: "User"
                )
                val userId = savedStateHandle.get<String>("userId")
                val participantId = savedStateHandle.get<String>("participantId")
                logEvent("userId: $userId, participantId: $participantId")
                if (userId != null && participantId != null) {
                    val participantsRequest = ParticipantsRequest(listOf(userId, participantId))
                    getChatRoom(participantsRequest)
                }
            }

            ChatEvent.OnDisconnect -> disconnect()
            ChatEvent.OnSendMessage -> sendMessage()
            is ChatEvent.OnMessageChange -> _messageText.value = chatEvent.message
        }
    }

    private suspend fun connectToChat(chatId: String, userId: String) {
        when (val result = chatSocketService.initSession(
            chatId = chatId,
            userId = userId
        )) {
            is GFResult.Success -> {
                chatSocketService.observeMessages().onEach { message ->
                    val newList = state.value.messages.toMutableList().apply {
                        add(0, message)
                    }
                    _state.value = state.value.copy(messages = newList)
                }.launchIn(viewModelScope)
            }

            is GFResult.Error -> {
                // emit message
                _toastEvent.emit("Unknown error")
            }
        }
    }

    private fun disconnect() {
        viewModelScope.launch {
            chatSocketService.closeSession()
        }
    }

    private suspend fun getAllMessages(chatRoomId: String) {
        _state.value = state.value.copy(isLoading = true)
        when (val result = messageService.getAllMessages(chatRoomId)) {
            is GFResult.Error -> {
                _state.value = _state.value.copy(
                    isLoading = false,
                    // has error!
                )
            }

            is GFResult.Success -> {
                _state.value = state.value.copy(
                    messages = result.data,
                    isLoading = false
                )
            }
        }
    }

    private fun getChatRoom(participants: ParticipantsRequest) {
        viewModelScope.launch {
            when (val result = chatRoomService.getOrCreateChatRoom(participants)) {
                is GFResult.Success -> {
                    logEvent(result.data.chatId)
                    getAllMessages(result.data.chatId)
                    connectToChat(result.data.chatId, participants.participants[0])
                }
                is GFResult.Error -> {
                    logEvent("Failed to get chat room")
                }
            }
        }
    }

    private fun sendMessage() {
        viewModelScope.launch {
            if (messageText.value.isNotBlank()) {
                chatSocketService.sendMessage(message = messageText.value)
                _messageText.value = ""
            }
        }
    }
}