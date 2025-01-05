package com.project.goldfish.model.event

sealed interface ChatEvent {
    data object OnSendMessage : ChatEvent
    data object OnDisconnect : ChatEvent
    data object OnConnect : ChatEvent
    data class OnMessageChange(val message: String) : ChatEvent
}