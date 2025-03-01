package com.project.goldfish.domain.friends

import com.project.goldfish.network.friends.FriendRepository
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult

interface AcceptFriendUseCase {
    suspend operator fun invoke(
        userId: String,
        friendId: String,
        firebaseUid: String
    ): GFResult<Unit, DataError.Network>
}

internal class AcceptFriendUseCaseImpl(
    private val friendRepository: FriendRepository
) : AcceptFriendUseCase {
    override suspend fun invoke(
        userId: String,
        friendId: String,
        firebaseUid: String
    ): GFResult<Unit, DataError.Network> =
        friendRepository.acceptFriend(userId = userId, firebaseUid = firebaseUid, friendId = friendId)
}