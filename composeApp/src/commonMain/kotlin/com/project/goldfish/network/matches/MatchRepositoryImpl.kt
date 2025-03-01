package com.project.goldfish.network.matches

import com.project.goldfish.data.MatchedUserDto
import com.project.goldfish.network.util.executePostForResponseBody
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.Parameters

class MatchRepositoryImpl(
    private val client: HttpClient
) : MatchRepository {
    override suspend fun retrieveMatch(firebaseUid: String, userId: String): GFResult<MatchedUserDto?, DataError.Network> =
        client.executePostForResponseBody<MatchedUserDto?>(
            endpoint = MatchRepository.Endpoints.GetMatch.url,
            firebaseToken = firebaseUid,
            formDataContent = FormDataContent(Parameters.build {
                append("userId", userId)
            }),
        )
}