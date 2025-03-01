package com.project.goldfish.network.moments

import com.project.goldfish.data.MomentDto
import com.project.goldfish.model.MomentData
import com.project.goldfish.serverName
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult
import kotlinx.serialization.Serializable

@Serializable
data class MomentInsertionRequest(
    val userId: String,
    val title: String,
    val description: String,
    val firebaseUid: String
)

interface MomentRepository {
    suspend fun getAllMoments(userId: String, firebaseToken: String): GFResult<List<MomentDto>, DataError.Network>
    suspend fun insertMoment(momentInsertionRequest: MomentInsertionRequest): GFResult<Unit, DataError.Network>

    companion object {
        val BASE_URL = "http://$serverName:8082"
    }

    sealed class Endpoints(val url: String) {
        data object GetAllMoments: Endpoints("$BASE_URL/moments")
        data object InsertMoment: Endpoints("$BASE_URL/insertMoment")
    }
}