package com.project.goldfish.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsernameViewModel: ViewModel() {
    private val _onJoinChat = MutableSharedFlow<UsernameState>()
    val onJoinChat = _onJoinChat.asSharedFlow()

    private val _state = MutableStateFlow(UsernameState())
    val state = _state.asStateFlow()

    fun onEvent(usernameEvent: UsernameEvent) {
        when (usernameEvent) {
            is UsernameEvent.OnUsernameChange -> {
                _state.value = _state.value.copy(username = usernameEvent.username)
            }

            is UsernameEvent.OnParticipantChange -> {
                _state.value = _state.value.copy(participant = usernameEvent.participant)
            }

            UsernameEvent.OnJoinClick -> onJoinClick()
        }
    }

    fun onJoinClick() {
        viewModelScope.launch {
            if(_state.value.username.isNotBlank() && _state.value.participant.isNotBlank()) {
                _onJoinChat.emit(_state.value)
            }
        }
    }
}

data class UsernameState(
    val username: String = "",
    val participant: String = ""
)

sealed class UsernameEvent {
    data class OnUsernameChange(val username: String): UsernameEvent()
    data class OnParticipantChange(val participant: String): UsernameEvent()
    data object OnJoinClick: UsernameEvent()
}
