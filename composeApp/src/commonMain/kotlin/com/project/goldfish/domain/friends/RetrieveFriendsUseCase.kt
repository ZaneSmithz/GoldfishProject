package com.project.goldfish.domain.friends

import com.project.goldfish.data.UserDto
import com.project.goldfish.network.friends.FriendRepository
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult

interface RetrieveFriendsUseCase {
    suspend operator fun invoke(firebaseUid: String, userId: String): GFResult<List<UserDto>, DataError.Network>
}

internal class RetrieveFriendsUseCaseImpl(
    private val friendRepository: FriendRepository
): RetrieveFriendsUseCase {
    override suspend fun invoke(
        firebaseUid: String,
        userId: String
    ): GFResult<List<UserDto>, DataError.Network> =
        friendRepository.retrieveFriends(firebaseUid = firebaseUid, userId =  userId)
}