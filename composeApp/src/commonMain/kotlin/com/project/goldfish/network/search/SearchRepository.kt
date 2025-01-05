package com.project.goldfish.network.search

import com.project.goldfish.data.UserDto
import com.project.goldfish.serverName
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult

interface SearchRepository {
    suspend fun searchUser(username: String, firebaseUid: String): GFResult<List<UserDto>, DataError.Network>

    companion object {
        val BASE_URL = "http://$serverName:8082"
    }

    sealed class Endpoints(val url: String) {
        data object SearchUser: Endpoints("$BASE_URL/searchuser")
    }
}