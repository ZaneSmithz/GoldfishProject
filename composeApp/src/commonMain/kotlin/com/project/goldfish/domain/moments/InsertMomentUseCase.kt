package com.project.goldfish.domain.moments

import com.project.goldfish.network.moments.MomentInsertionRequest
import com.project.goldfish.network.moments.MomentRepository
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult

interface InsertMomentUseCase {
    suspend operator fun invoke(momentInsertionRequest: MomentInsertionRequest): GFResult<Unit, DataError.Network>
}

class InsertMomentUseCaseImpl(
    private val momentRepository: MomentRepository
) : InsertMomentUseCase {
    override suspend fun invoke(momentInsertionRequest: MomentInsertionRequest): GFResult<Unit, DataError.Network> =
        momentRepository.insertMoment(momentInsertionRequest)
}
