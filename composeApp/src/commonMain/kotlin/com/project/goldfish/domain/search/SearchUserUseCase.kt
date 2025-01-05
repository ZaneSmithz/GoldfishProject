package com.project.goldfish.domain.search

import com.project.goldfish.data.UserDto
import com.project.goldfish.network.search.SearchRepository
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult

interface SearchUserUseCase {
    suspend operator fun invoke(username: String, firebaseUid: String): GFResult<List<UserDto>, DataError.Network>
}

class SearchUserUseCaseImpl(
    private val searchRepository: SearchRepository
) : SearchUserUseCase {
    override suspend fun invoke(username: String, firebaseUid: String): GFResult<List<UserDto>, DataError.Network> =
       searchRepository.searchUser(username = username, firebaseUid = firebaseUid)
}