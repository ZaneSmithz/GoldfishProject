package com.project.goldfish.model.state

import com.project.goldfish.model.MessageData

data class ChatState(
    val messages: List<MessageData> = emptyList(),
    val isLoading: Boolean = false,
    val username: String = "User",
    val chatName: String? = null
)
