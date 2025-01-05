package com.project.goldfish.domain.login

import com.project.goldfish.data.UserDto
import com.project.goldfish.network.auth.LoginRepository
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult


interface AddUserUseCase {
    suspend operator fun invoke(username: String, firebaseUid: String): GFResult<UserDto?, DataError.Network>
}

internal class AddUserUseCaseImpl(
    private val loginRepository: LoginRepository
): AddUserUseCase {
    override suspend fun invoke(username: String, firebaseUid: String): GFResult<UserDto?, DataError.Network> =
        loginRepository.addUser(username, firebaseUid)
}