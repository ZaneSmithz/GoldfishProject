package com.project.goldfish.network.friends

import com.project.goldfish.data.UserDto
import com.project.goldfish.network.util.executePostForResponseBody
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.Parameters

class FriendRepositoryImpl(
    private val client: HttpClient
) : FriendRepository {
    override suspend fun retrieveFriends(
        firebaseUid: String,
        userId: String
    ): GFResult<List<UserDto>, DataError.Network> =
        client.executePostForResponseBody<List<UserDto>>(
            endpoint = FriendRepository.Endpoints.GetFriends.url,
            formDataContent = FormDataContent(Parameters.build {
                append("userId", userId)
            }),
            firebaseToken = firebaseUid
        )

    override suspend fun retrieveRequestedFriends(
        firebaseUid: String,
        userId: String
    ): GFResult<List<UserDto>, DataError.Network> =
        client.executePostForResponseBody<List<UserDto>>(
            endpoint = FriendRepository.Endpoints.GetFriendRequests.url,
            formDataContent = FormDataContent(Parameters.build {
                append("userId", userId)
            }),
            firebaseToken = firebaseUid
        )

    override suspend fun addFriend(
        userId: String,
        friendId: String,
        firebaseUid: String
    ): GFResult<String, DataError.Network> =
        client.executePostForResponseBody<String>(
            endpoint = FriendRepository.Endpoints.AddFriend.url,
            formDataContent = FormDataContent(Parameters.build {
                append("userId", userId)
                append("friendId", friendId)
            }),
            firebaseToken = firebaseUid
        )


    override suspend fun acceptFriend(
        userId: String,
        friendId: String,
        firebaseUid: String
    ): GFResult<Unit, DataError.Network> =
        client.executePostForResponseBody<Unit>(
            endpoint = FriendRepository.Endpoints.AcceptRequests.url,
            formDataContent = FormDataContent(Parameters.build {
                append("userId", userId)
                append("friendId", friendId)
            }),
            firebaseToken = firebaseUid
        )
}