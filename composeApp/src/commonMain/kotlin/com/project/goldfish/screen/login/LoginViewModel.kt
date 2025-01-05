package com.project.goldfish.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.goldfish.SessionManager
import com.project.goldfish.domain.login.RetrieveUserUseCase
import com.project.goldfish.logEvent
import com.project.goldfish.model.event.LoginEvent
import com.project.goldfish.network.auth.LoginRepository
import com.project.goldfish.util.GFResult
import kotlinx.coroutines.launch

class LoginViewModel(
    private val sessionManager: SessionManager,
    private val retrieveUserUseCase: RetrieveUserUseCase
): ViewModel() {
    val sessionState = sessionManager.state

    fun onEvent(loginEvent: LoginEvent) {
        when (loginEvent) {
            is LoginEvent.OnLoginClick -> {
                onLoginClick(loginEvent.firebaseUid)
            }
        }
    }

   private fun onLoginClick(token: String) {
        viewModelScope.launch {
           when(val result = retrieveUserUseCase.invoke(token)) {
               is GFResult.Error -> Unit // error handling
               is GFResult.Success -> {
                   logEvent("USER = ${result.data}")
                   result.data?.let { user ->
                       sessionManager.updateSession(user, token)
                   }
               }
           }
        }
    }
}