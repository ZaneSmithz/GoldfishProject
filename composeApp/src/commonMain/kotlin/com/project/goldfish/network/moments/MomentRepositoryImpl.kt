package com.project.goldfish.network.moments

import com.project.goldfish.data.MomentDto
import com.project.goldfish.model.MomentData
import com.project.goldfish.network.util.executePostForResponseBody
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.Parameters
import kotlinx.serialization.Serializable

@Serializable
data class MomentRequest(val userId: Int)

class MomentRepositoryImpl(
    private val client: HttpClient
) : MomentRepository {
    override suspend fun getAllMoments(userId: String, firebaseToken: String) =
        client.executePostForResponseBody<List<MomentDto>>(
            endpoint = MomentRepository.Endpoints.GetAllMoments.url,
            formDataContent = FormDataContent(Parameters.build {
                append("userId", userId)
            }),
            firebaseToken = firebaseToken
        )

    override suspend fun insertMoment(momentInsertionRequest: MomentInsertionRequest) =
        client.executePostForResponseBody<Unit>(
            endpoint = MomentRepository.Endpoints.InsertMoment.url,
            formDataContent = FormDataContent(Parameters.build {
                append("userId", momentInsertionRequest.userId)
                append("title", momentInsertionRequest.title)
                append("description", momentInsertionRequest.description)
            }),
            firebaseToken = momentInsertionRequest.firebaseUid
        )
}