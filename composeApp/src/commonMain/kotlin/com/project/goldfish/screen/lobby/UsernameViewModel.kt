package com.project.goldfish.screen.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.goldfish.SessionManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsernameViewModel(
    sessionManager: SessionManager
): ViewModel() {
    private val _onJoinChat = MutableSharedFlow<UsernameState>()
    val onJoinChat = _onJoinChat.asSharedFlow()

    private val _state = MutableStateFlow(UsernameState())
    val state = _state.asStateFlow()

    init {
        sessionManager.state.value.user?.let {
            _state.value = _state.value.copy(username = it.id.toString())
        }
    }
    fun onEvent(usernameEvent: UsernameEvent) {
        when (usernameEvent) {
            is UsernameEvent.OnParticipantChange -> {
                _state.value = _state.value.copy(participant = usernameEvent.participant)
            }

            UsernameEvent.OnJoinClick -> onJoinClick()
        }
    }

    private fun onJoinClick() {
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

sealed interface UsernameEvent {
    data class OnParticipantChange(val participant: String): UsernameEvent
    data object OnJoinClick: UsernameEvent
}
