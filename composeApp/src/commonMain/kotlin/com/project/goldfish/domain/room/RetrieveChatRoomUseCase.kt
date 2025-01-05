package com.project.goldfish.domain.room

import com.project.goldfish.data.ChatRoomDto
import com.project.goldfish.network.messages.ParticipantsRequest
import com.project.goldfish.network.rooms.ChatRoomRepository
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult

interface RetrieveChatRoomUseCase {
    suspend operator fun invoke(participantsRequest: ParticipantsRequest): GFResult<ChatRoomDto, DataError.Network>
}

class RetrieveChatRoomUseCaseImpl(
    private val chatRoomRepository: ChatRoomRepository
) : RetrieveChatRoomUseCase {
    override suspend fun invoke(participantsRequest: ParticipantsRequest): GFResult<ChatRoomDto, DataError.Network> =
        chatRoomRepository.getOrCreateChatRoom(participantsRequest)
}