package com.project.goldfish.domain.matches

import com.project.goldfish.data.MatchedUserDto
import com.project.goldfish.network.matches.MatchRepository
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult

interface RetrieveMatchUseCase {
    suspend operator fun invoke(userId: String, firebaseUid: String): GFResult<MatchedUserDto?, DataError.Network>
}

class RetrieveMatchUseCaseImpl(
    private val matchRepository: MatchRepository
) : RetrieveMatchUseCase {
    override suspend fun invoke(userId: String, firebaseUid: String): GFResult<MatchedUserDto?, DataError.Network> =
       matchRepository.retrieveMatch(userId = userId, firebaseUid = firebaseUid)
}
