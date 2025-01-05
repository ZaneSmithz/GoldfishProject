package com.project.goldfish.model.event

sealed interface RegistrationEvent {
    data class OnRegisterClick(val firebaseUid: String, val username: String): RegistrationEvent
}