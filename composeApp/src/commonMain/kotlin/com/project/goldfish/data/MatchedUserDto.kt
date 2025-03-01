package com.project.goldfish.data

import kotlinx.serialization.Serializable

@Serializable
data class MatchedUserDto(
    val user: UserDto? = null,
    val createdAt: Long? = null
)