package com.project.goldfish.data

import com.project.goldfish.model.MessageData
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
    fun toMessage(): MessageData {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        return MessageData(
            text = text,
            formattedTime = localDate,
            userId = userId
        )
    }
}