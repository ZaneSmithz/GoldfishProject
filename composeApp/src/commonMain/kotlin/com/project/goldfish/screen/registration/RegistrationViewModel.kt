package com.project.goldfish.screen.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.goldfish.SessionManager
import com.project.goldfish.domain.login.AddUserUseCase
import com.project.goldfish.logEvent
import com.project.goldfish.model.event.RegistrationEvent
import com.project.goldfish.network.auth.LoginRepository
import com.project.goldfish.util.GFResult
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val addUserUseCase: AddUserUseCase,
    private val sessionManager: SessionManager
): ViewModel() {

    val sessionState = sessionManager.state

    fun onEvent(registrationEvent: RegistrationEvent) {
        when (registrationEvent) {
            is RegistrationEvent.OnRegisterClick -> {
                onRegistrationClick(registrationEvent.firebaseUid, username = registrationEvent.username)
            }
        }
    }

    private fun onRegistrationClick(token: String, username: String) {
        viewModelScope.launch {
            when(val result = addUserUseCase.invoke(firebaseUid = token, username = username)) {
                is GFResult.Error -> Unit // error handling
                is GFResult.Success -> {
                    logEvent("USER = ${result.data}")
                    result.data?.let { user ->
                        sessionManager.updateSession(user = user, token = token)
                    }
                }
            }
        }
    }
}