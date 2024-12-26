package com.project.goldfish.network.auth

import com.project.goldfish.data.UserDto
import com.project.goldfish.logEvent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType

class LoginServiceImpl(
    private val client: HttpClient
): LoginService {
    override suspend fun retrieveUser(firebaseUid: String): UserDto? {
        return try {
            client.get(LoginService.Endpoints.GetUser.url) {
                header("Authorization", "Bearer $firebaseUid")
                contentType(ContentType.Application.Json)
            }.body<UserDto>()
        } catch (e: Exception) {
            logEvent("${e.message}")
            e.printStackTrace()
            null
        }
    }

    override suspend fun addUser(firebaseUid: String): UserDto? {
        return try {
            client.post(LoginService.Endpoints.AddUser.url) {
                contentType(ContentType.Application.Json)
            }.body<UserDto>()
        } catch (e: Exception) {
            logEvent("${e.message}")
            e.printStackTrace()
            null
        }
    }
}