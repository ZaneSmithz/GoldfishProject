package com.project.goldfish.network.matches

import com.project.goldfish.data.UserDto
import com.project.goldfish.network.util.executeGetForResponseBody
import com.project.goldfish.network.util.executePostForResponseBody
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.Parameters

class MatchRepositoryImpl(
    private val client: HttpClient
) : MatchRepository {
    override suspend fun retrieveMatch(firebaseUid: String, userId: String): GFResult<UserDto?, DataError.Network> =
        client.executePostForResponseBody<UserDto?>(
            endpoint = MatchRepository.Endpoints.GetMatch.url,
            firebaseUid = firebaseUid,
            formDataContent = FormDataContent(Parameters.build {
                append("userId", userId)
            }),
        )
}