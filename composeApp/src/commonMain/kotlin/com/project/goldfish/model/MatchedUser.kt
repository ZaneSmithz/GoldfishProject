package com.project.goldfish.model

import com.project.goldfish.data.UserDto
import kotlinx.datetime.LocalDateTime

data class MatchedUser(
    val userDto: UserDto,
    val createdAt: LocalDateTime
)
