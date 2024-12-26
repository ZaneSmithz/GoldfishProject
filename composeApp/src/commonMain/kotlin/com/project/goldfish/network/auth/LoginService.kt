package com.project.goldfish.network.auth

import com.project.goldfish.data.UserDto
import com.project.goldfish.serverName

interface LoginService {
    suspend fun retrieveUser(firebaseUid: String): UserDto?
    suspend fun addUser(firebaseUid: String): UserDto?

    companion object {
        val BASE_URL = "http://$serverName:8082"
    }

    sealed class Endpoints(val url: String) {
        data object GetUser: Endpoints("$BASE_URL/user")
        data object AddUser: Endpoints("$BASE_URL/adduser")
    }
}

