package com.project.goldfish.domain.login

import com.project.goldfish.data.UserDto
import com.project.goldfish.network.auth.LoginRepository
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult

interface RetrieveUserUseCase {
    suspend operator fun invoke(firebaseUid: String): GFResult<UserDto?, DataError.Network>
}

internal class RetrieveUserUseCaseImpl(
    private val loginRepository: LoginRepository
): RetrieveUserUseCase {
    override suspend fun invoke(firebaseUid: String): GFResult<UserDto?, DataError.Network> =
        loginRepository.retrieveUser(firebaseUid)
}