package com.project.goldfish.model.event

sealed interface LobbyEvent {
    data class OnJoinClick(val friendId: String) : LobbyEvent
    data class OnFriendTextChange(val text: String) : LobbyEvent
    data class OnAddFriend(val friendId: String) : LobbyEvent
    data class OnAcceptFriend(val friendId: String) : LobbyEvent
    data class OnInsertMoment(val title: String, val description: String) : LobbyEvent
}