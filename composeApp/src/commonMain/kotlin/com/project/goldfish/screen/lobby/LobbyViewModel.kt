package com.project.goldfish.screen.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.goldfish.SessionManager
import com.project.goldfish.domain.search.SearchUserUseCase
import com.project.goldfish.domain.friends.AcceptFriendUseCase
import com.project.goldfish.domain.friends.AddFriendUseCase
import com.project.goldfish.domain.friends.RetrieveFriendsUseCase
import com.project.goldfish.domain.friends.RetrieveRequestedFriendsUseCase
import com.project.goldfish.domain.matches.RetrieveMatchUseCase
import com.project.goldfish.logEvent
import com.project.goldfish.model.event.LobbyEvent
import com.project.goldfish.model.request.ChatRequest
import com.project.goldfish.model.state.LobbyState
import com.project.goldfish.util.GFResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LobbySessionDetails(
    val userId: String = "",
    val firebaseId: String = ""
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class LobbyViewModel(
    private val sessionManager: SessionManager,
    private val acceptFriendUseCase: AcceptFriendUseCase,
    private val addFriendUseCase: AddFriendUseCase,
    private val retrieveFriendsUseCase: RetrieveFriendsUseCase,
    private val retrieveRequestedFriendsUseCase: RetrieveRequestedFriendsUseCase,
    private val searchUserUseCase: SearchUserUseCase,
    private val retrieveMatchUseCase: RetrieveMatchUseCase
) : ViewModel() {
    private val _onJoinChat = MutableSharedFlow<ChatRequest>()
    val onJoinChat = _onJoinChat.asSharedFlow()

    private val _state = MutableStateFlow(LobbyState())
    val state = _state.asStateFlow()


    init {
        viewModelScope.launch {
            sessionManager.state.transformLatest {
                if (it.user != null) {
                    it.token?.let { token ->
                        emit(
                            LobbySessionDetails(
                                userId = it.user.id.toString(),
                                firebaseId = token
                            )
                        )
                    }
                }
            }.stateIn(this).collect { sessionDetails ->
                println("session details = $sessionDetails")
                _state.update { state ->
                    state.copy(
                        sessionDetails = sessionDetails,
                    )
                }
                retrieveFriends(userId = sessionDetails.userId, token = sessionDetails.firebaseId)
                retrieveRequestedFriends(
                    userId = sessionDetails.userId,
                    token = sessionDetails.firebaseId
                )
                retrieveMatch(sessionDetails)
            }
        }
        retrieveSearchResultContent()
    }

    fun onEvent(usernameEvent: LobbyEvent) {
        when (usernameEvent) {
            is LobbyEvent.OnJoinClick -> onJoinClick(usernameEvent.friendId)
            is LobbyEvent.OnAddFriend -> {
                viewModelScope.launch {
                    when (val result =
                        addFriendUseCase.invoke(
                            userId = sessionManager.state.value.user?.id.toString(),
                            friendId = usernameEvent.friendId,
                            firebaseUid = sessionManager.state.value.token ?: return@launch
                        )) {
                        is GFResult.Error -> {
                            logEvent("Unknown Error!")
                        }

                        is GFResult.Success -> {
                            retrieveFriends(
                                userId = state.value.sessionDetails.userId,
                                token = state.value.sessionDetails.firebaseId
                            )
                            _state.value = _state.value.copy(friendIdText = result.data)
                        }
                    }
                }
            }

            is LobbyEvent.OnFriendTextChange -> {
                _state.value = _state.value.copy(friendIdText = usernameEvent.text)
            }

            is LobbyEvent.OnAcceptFriend -> acceptFriend(usernameEvent.friendId)
        }
    }

    private suspend fun retrieveFriends(userId: String, token: String) {
        when (val result = retrieveFriendsUseCase.invoke(
            userId = userId,
            firebaseUid = token,
        )) {
            is GFResult.Error -> println("result retrive friends ${result.error}")
            is GFResult.Success -> {
                println("result retrive friends ${result.data}")
                _state.value = _state.value.copy(friendList = result.data)
            }
        }
    }

    private suspend fun retrieveRequestedFriends(userId: String, token: String) {
        when (val result = retrieveRequestedFriendsUseCase.invoke(
            userId = userId,
            firebaseUid = token,
        )) {
            is GFResult.Error -> Unit // error handling
            is GFResult.Success -> _state.value =
                _state.value.copy(requestedFriendList = result.data)
        }
    }

    private fun acceptFriend(friendId: String) {
        viewModelScope.launch {
            when (val result = acceptFriendUseCase.invoke(
                userId = sessionManager.state.value.user?.id.toString(),
                friendId = friendId,
                firebaseUid = sessionManager.state.value.token ?: return@launch
            )) {
                is GFResult.Error -> Unit // error handling
                is GFResult.Success -> _state.value = _state.value.copy(friendList = result.data)
            }
        }
    }

    private suspend fun retrieveMatch(sessionDetails: LobbySessionDetails) {
        when (val result = retrieveMatchUseCase.invoke(
            userId = sessionDetails.userId,
            firebaseUid = sessionDetails.firebaseId
        )) {
            is GFResult.Error -> {
                println("error match can't be retrieved")
            }
            is GFResult.Success -> {
                println("match = ${result.data}")
                _state.update { state ->
                    state.copy(matchedUser = result.data)
                }
            }
        }
    }


    private fun onJoinClick(participantId: String) {
        viewModelScope.launch {
            if (_state.value.userId.isNotBlank() && participantId.isNotBlank()) {
                _onJoinChat.emit(
                    ChatRequest(
                        userId = _state.value.userId,
                        participantId = participantId
                    )
                )
            }
        }
    }

    private fun retrieveSearchResultContent() {
        viewModelScope.launch {
            _state.transformLatest { emit(it.friendIdText) }
                .debounce(1000L)
                .stateIn(this)
                .collect { debouncedText ->
                    when (val result = searchUserUseCase.invoke(
                        username = debouncedText,
                        firebaseUid = state.value.sessionDetails.firebaseId
                    )) {
                        is GFResult.Error -> {
                            logEvent("Unknown Error!") // error handle
                        }

                        is GFResult.Success -> {
                            logEvent("friend search list = ${result.data}")
                            _state.value =
                                _state.value.copy(friendSearchList = result.data ?: emptyList())
                        }
                    }
                }
        }
    }
}

