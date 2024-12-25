package com.project.goldfish.data

import kotlinx.serialization.Serializable

@Serializable
data class ChatRoomDto(
    val chatId: String,
    val participants: List<String>,
    val lastUpdated: Long,
)