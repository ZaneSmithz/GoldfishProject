package com.project.goldfish.network.matches

import com.project.goldfish.data.MatchedUserDto
import com.project.goldfish.data.UserDto
import com.project.goldfish.serverName
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult

interface MatchRepository {
    suspend fun retrieveMatch(firebaseUid: String, userId: String): GFResult<MatchedUserDto?, DataError.Network>

    companion object {
        val BASE_URL = "http://$serverName:8082"
    }

    sealed class Endpoints(val url: String) {
        data object GetMatch: Endpoints("$BASE_URL/retrievematch")
    }
}

