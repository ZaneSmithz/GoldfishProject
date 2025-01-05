package com.project.goldfish.network.search

import com.project.goldfish.data.UserDto
import com.project.goldfish.network.util.executePostForResponseBody
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType

class SearchRepositoryImpl(
    private val client: HttpClient
) : SearchRepository {
    override suspend fun searchUser(
        username: String,
        firebaseUid: String
    ): GFResult<List<UserDto>, DataError.Network> =
        client.executePostForResponseBody<List<UserDto>>(
            endpoint = SearchRepository.Endpoints.SearchUser.url,
            formDataContent = FormDataContent(Parameters.build {
                append("username", username)
            }),
            firebaseUid = firebaseUid
        )
}
