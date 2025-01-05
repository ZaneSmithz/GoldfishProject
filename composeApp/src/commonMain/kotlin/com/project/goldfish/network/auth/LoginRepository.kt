package com.project.goldfish.network.auth

import com.project.goldfish.data.UserDto
import com.project.goldfish.serverName
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult

interface LoginRepository {
    suspend fun retrieveUser(firebaseUid: String): GFResult<UserDto?, DataError.Network>
    suspend fun addUser(username: String, firebaseUid: String): GFResult<UserDto?, DataError.Network>

    companion object {
        val BASE_URL = "http://$serverName:8082"
    }

    sealed class Endpoints(val url: String) {
        data object GetUser: Endpoints("$BASE_URL/user")
        data object AddUser: Endpoints("$BASE_URL/adduser")
    }
}

