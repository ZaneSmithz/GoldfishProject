package com.project.goldfish.domain.messages

import com.project.goldfish.model.MessageData
import com.project.goldfish.network.messages.MessageRepository
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult

interface GetAllMessagesUseCase {
    suspend operator fun invoke(chatRoomId: String): GFResult<List<MessageData>, DataError.Network>
}

class GetAllMessagesUseCaseImpl(
    private val messageRepository: MessageRepository
) : GetAllMessagesUseCase {
    override suspend fun invoke(chatRoomId: String): GFResult<List<MessageData>, DataError.Network> =
        messageRepository.getAllMessages(chatRoomId)
}
