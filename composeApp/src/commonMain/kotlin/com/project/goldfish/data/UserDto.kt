package com.project.goldfish.data

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id : Int,
    val username: String,
    val firebaseUid: String,
    val profilePic: String?,
    val lastUserInteraction: Long? = null
)