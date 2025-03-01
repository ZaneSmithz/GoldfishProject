package com.project.goldfish.data

import com.project.goldfish.model.MessageData
import com.project.goldfish.model.MomentData
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class MomentDto(
    val user: UserDto,
    val title: String,
    val description: String,
    val timestamp: Long,
    val id: String
) {
    fun toMoment(): MomentData {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        return MomentData(
            title = title,
            description = description,
            formattedTime = localDate,
            user = user
        )
    }
}


