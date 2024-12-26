package com.project.goldfish.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.goldfish.SessionManager
import com.project.goldfish.logEvent
import com.project.goldfish.network.auth.LoginService
import com.project.goldfish.screen.lobby.UsernameState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginService: LoginService,
    private val sessionManager: SessionManager
): ViewModel() {
    val sessionState = sessionManager.state

    fun onEvent(loginEvent: LoginEvent) {
        when (loginEvent) {
            is LoginEvent.OnLoginClick -> {
                onLoginClick(loginEvent.firebaseUid)
            }
        }
    }

   private fun onLoginClick(firebaseUid: String) {
        viewModelScope.launch {
            val user = loginService.retrieveUser(firebaseUid)
            logEvent("USER = $user")
            user?.let {
                sessionManager.updateSession(user)
            }
        }
    }
}


sealed interface LoginEvent {
    data class OnLoginClick(val firebaseUid: String): LoginEvent
}