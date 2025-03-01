package com.project.goldfish.network.friends

import com.project.goldfish.data.UserDto
import com.project.goldfish.serverName
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult

interface FriendRepository {
    suspend fun retrieveFriends(firebaseUid: String, userId: String): GFResult<List<UserDto>, DataError.Network>
    suspend fun retrieveRequestedFriends(firebaseUid: String, userId: String): GFResult<List<UserDto>, DataError.Network>
    suspend fun addFriend(userId: String, friendId: String, firebaseUid: String):GFResult<String, DataError.Network>
    suspend fun acceptFriend(userId: String, friendId: String, firebaseUid: String): GFResult<Unit, DataError.Network>

    companion object {
        val BASE_URL = "http://$serverName:8082"
    }

    sealed class Endpoints(val url: String) {
        data object AddFriend: Endpoints("$BASE_URL/addfriend")
        data object GetFriendRequests: Endpoints("$BASE_URL/friendrequests")
        data object GetFriends: Endpoints("$BASE_URL/friends")
        data object AcceptRequests: Endpoints("$BASE_URL/acceptfriendrequest")
    }
}

