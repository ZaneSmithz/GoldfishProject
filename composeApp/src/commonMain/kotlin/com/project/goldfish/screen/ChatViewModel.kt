package com.project.goldfish.screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.goldfish.domain.ChatState
import com.project.goldfish.logEvent
import com.project.goldfish.network.socket.ChatSocketService
import com.project.goldfish.network.messages.MessageService
import com.project.goldfish.network.messages.ParticipantsRequest
import com.project.goldfish.network.rooms.ChatRoomService
import com.project.goldfish.util.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


sealed interface ChatEvent {
    data object OnSendMessage : ChatEvent
    data object OnDisconnect : ChatEvent
    data object OnConnect : ChatEvent
    data class OnMessageChange(val message: String) : ChatEvent
}

class ChatViewModel(
    private val messageService: MessageService,
    private val chatSocketService: ChatSocketService,
    private val chatRoomService: ChatRoomService,
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
                val userId = savedStateHandle.get<String>("userId")
                val participantId = savedStateHandle.get<String>("participantId")
                logEvent("userId: $userId, participantId: $participantId")

                if(userId != null && participantId != null) {
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
            is Resource.Success -> {
                chatSocketService.observeMessages().onEach { message ->
                    val newList = state.value.messages.toMutableList().apply {
                        add(0, message)
                    }
                    _state.value = state.value.copy(messages = newList)
                }.launchIn(viewModelScope)
            }

            is Resource.Error -> {
                _toastEvent.emit(result.message ?: "Unknown error")
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
        _state.value = state.value.copy(
            messages = messageService.getAllMessages(chatRoomId),
            isLoading = false
        )
    }

    private fun getChatRoom(participants: ParticipantsRequest) {
        viewModelScope.launch {
            when (val result = chatRoomService.getOrCreateChatRoom(participants)) {
                is Resource.Success -> {
                    result.data?.chatId?.let { chatId ->
                        logEvent(chatId)
                        getAllMessages(chatId)
                        connectToChat(chatId, participants.participants[0])
                    }
                }

                is Resource.Error -> {
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