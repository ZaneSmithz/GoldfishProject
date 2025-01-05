package com.project.goldfish.model.event

sealed interface LoginEvent {
    data class OnLoginClick(val firebaseUid: String): LoginEvent
}