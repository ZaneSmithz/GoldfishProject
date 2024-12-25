package com.project.goldfish.data

import com.project.goldfish.domain.Message
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val chatId: String,
    val userId: String,
    val text: String,
    val timestamp: Long,
    val id: String
) {
    fun toMessage(): Message {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        return Message(
            text = text,
            formattedTime = localDate,
            userId = userId
        )
    }
}