package com.project.goldfish.domain

import kotlinx.datetime.LocalDateTime

data class Message(
    val text: String,
    val formattedTime: LocalDateTime,
    val userId: String,
)
