package com.project.goldfish.domain.moments

import com.project.goldfish.model.MomentData
import com.project.goldfish.network.moments.MomentRepository
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult

interface GetAllMomentsUseCase {
    suspend operator fun invoke(userId: String, firebaseToken: String): List<MomentData>
}

class GetAllMomentsUseCaseImpl(
    private val momentRepository: MomentRepository
) : GetAllMomentsUseCase {
    override suspend fun invoke(userId: String, firebaseToken: String): List<MomentData> =
        when(val result =  momentRepository.getAllMoments(userId, firebaseToken)) {
            is GFResult.Error -> emptyList()
            is GFResult.Success -> {
                result.data.map { it.toMoment() }
            }
        }
}
