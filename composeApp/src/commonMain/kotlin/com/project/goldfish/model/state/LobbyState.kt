package com.project.goldfish.model.state

import com.project.goldfish.data.UserDto
import com.project.goldfish.screen.lobby.LobbySessionDetails

data class LobbyState(
    val userId: String = "",
    val matchedUser: UserDto? = null,
    val friendIdText: String = "",
    val friendList: List<UserDto> = emptyList(),
    val requestedFriendList: List<UserDto> = emptyList(),
    val friendSearchList: List<UserDto> = emptyList(),
    val sessionDetails: LobbySessionDetails = LobbySessionDetails()
)