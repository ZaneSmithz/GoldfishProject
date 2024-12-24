package com.project.goldfish.domain

data class ChatState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val chatName: String? = null
)
