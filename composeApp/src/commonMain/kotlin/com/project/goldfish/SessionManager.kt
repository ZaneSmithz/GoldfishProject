package com.project.goldfish

import com.project.goldfish.data.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SessionState(
    val user: UserDto? = null
)
interface SessionManager {
    val state: StateFlow<SessionState>
    fun updateSession(user: UserDto)
}

class SessionManagerImpl: SessionManager {
    private val _state = MutableStateFlow(SessionState())
    override val state = _state.asStateFlow()

    override fun updateSession(user: UserDto) {
        _state.value = _state.value.copy(user = user)
    }
}