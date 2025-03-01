package com.project.goldfish.model.state

import com.project.goldfish.data.MatchedUserDto
import com.project.goldfish.data.UserDto
import com.project.goldfish.model.MomentData
import com.project.goldfish.screen.lobby.LobbySessionDetails

data class LobbyState(
    val matchedUser: MatchedUserDto? = null,
    val friendIdText: String = "",
    val friendList: List<UserDto> = emptyList(),
    val requestedFriendList: List<UserDto> = emptyList(),
    val friendSearchList: List<UserDto> = emptyList(),
    val momentList: List<MomentData> = emptyList(),
    val sessionDetails: LobbySessionDetails = LobbySessionDetails(),
    val remainingTime: String = "00:00",
)