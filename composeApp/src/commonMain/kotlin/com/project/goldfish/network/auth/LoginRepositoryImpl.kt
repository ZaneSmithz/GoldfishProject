package com.project.goldfish.network.auth

import com.project.goldfish.data.UserDto
import com.project.goldfish.logEvent
import com.project.goldfish.network.util.executeGetForResponseBody
import com.project.goldfish.network.util.executePostForResponseBody
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType

class LoginRepositoryImpl(
    private val client: HttpClient
) : LoginRepository {
    override suspend fun retrieveUser(firebaseUid: String): GFResult<UserDto?, DataError.Network> =
        client.executeGetForResponseBody<UserDto?>(
            endpoint = LoginRepository.Endpoints.GetUser.url,
            firebaseUid = firebaseUid
        )

    override suspend fun addUser(
        username: String,
        firebaseUid: String
    ): GFResult<UserDto?, DataError.Network> =
        client.executePostForResponseBody<UserDto?>(
            endpoint = LoginRepository.Endpoints.AddUser.url,
            formDataContent = FormDataContent(Parameters.build {
                append("username", username)
                append("profilePic", "profile picture")
            }),
            firebaseUid = firebaseUid
        )
}