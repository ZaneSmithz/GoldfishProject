package com.project.goldfish.model.state

import com.project.goldfish.data.UserDto

data class LobbyState(
    val userId: String = "",
    val friendIdText: String = "",
    val friendList: List<UserDto> = emptyList(),
    val requestedFriendList: List<UserDto> = emptyList(),
    val friendSearchList: List<UserDto> = emptyList()
)