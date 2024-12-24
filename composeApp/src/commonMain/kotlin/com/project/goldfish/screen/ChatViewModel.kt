package com.project.goldfish.screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.goldfish.domain.ChatState
import com.project.goldfish.logEvent
import com.project.goldfish.network.ChatSocketService
import com.project.goldfish.network.MessageService
import com.project.goldfish.util.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ChatViewModel(
    private val messageService: MessageService,
    private val chatSocketService: ChatSocketService,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {


    private var _messageText = mutableStateOf("")
    val messageText: State<String> = _messageText

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    fun connectToChat() {
        logEvent("CONNNECTING TO CHAT VM")
        getAllMessages()
        savedStateHandle.get<String>("username")?.let { username ->
            logEvent("USERNAME = $username")
            viewModelScope.launch {
                when(val result = chatSocketService.initSession(username)) {
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
        }
    }

    fun onMessageChange(message: String) {
        _messageText.value = message
    }

    fun disconnect() {
        viewModelScope.launch {
            chatSocketService.closeSession()
        }
    }

    private fun getAllMessages() {
        viewModelScope.launch {
            logEvent("ALL MESSAGES = ${messageService.getAllMessages()}")
            _state.value = state.value.copy(isLoading = true)
            _state.value = state.value.copy(
                messages = messageService.getAllMessages(),
                isLoading = false
            )
        }
    }

    fun sendMessage() {
        viewModelScope.launch {
            if (messageText.value.isNotBlank()) {
                chatSocketService.sendMessage(message = messageText.value)
                _messageText.value = ""
            }
        }
    }
}