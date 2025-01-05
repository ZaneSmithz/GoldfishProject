package com.project.goldfish.domain.friends

import com.project.goldfish.network.friends.FriendRepository
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult

interface AddFriendUseCase {
    suspend operator fun invoke(
        userId: String,
        friendId: String,
        firebaseUid: String
    ): GFResult<String, DataError.Network>
}

internal class AddFriendUseCaseImpl(
    private val friendRepository: FriendRepository
) : AddFriendUseCase {
    override suspend fun invoke(
        userId: String,
        friendId: String,
        firebaseUid: String
    ): GFResult<String, DataError.Network> =
        friendRepository.addFriend(userId = userId, firebaseUid = firebaseUid, friendId = friendId)
}