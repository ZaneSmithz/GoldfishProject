package com.project.goldfish.domain.friends

import com.project.goldfish.data.UserDto
import com.project.goldfish.network.friends.FriendRepository
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult

interface RetrieveRequestedFriendsUseCase {
    suspend operator fun invoke(firebaseUid: String, userId: String): GFResult<List<UserDto>, DataError.Network>
}

internal class RetrieveRequestedFriendsUseCaseImpl(
    private val friendRepository: FriendRepository
): RetrieveRequestedFriendsUseCase {
    override suspend fun invoke(
        firebaseUid: String,
        userId: String
    ): GFResult<List<UserDto>, DataError.Network> =
        friendRepository.retrieveRequestedFriends(firebaseUid = firebaseUid, userId = userId)
}