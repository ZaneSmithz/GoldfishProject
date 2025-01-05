package com.project.goldfish.screen.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.goldfish.SessionManager
import com.project.goldfish.domain.search.SearchUserUseCase
import com.project.goldfish.domain.friends.AcceptFriendUseCase
import com.project.goldfish.domain.friends.AddFriendUseCase
import com.project.goldfish.domain.friends.RetrieveFriendsUseCase
import com.project.goldfish.domain.friends.RetrieveRequestedFriendsUseCase
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
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class LobbyViewModel(
    private val sessionManager: SessionManager,
    private val acceptFriendUseCase: AcceptFriendUseCase,
    private val addFriendUseCase: AddFriendUseCase,
    private val retrieveFriendsUseCase: RetrieveFriendsUseCase,
    private val retrieveRequestedFriendsUseCase: RetrieveRequestedFriendsUseCase,
    private val searchUserUseCase: SearchUserUseCase,
) : ViewModel() {
    private val _onJoinChat = MutableSharedFlow<ChatRequest>()
    val onJoinChat = _onJoinChat.asSharedFlow()

    private val _state = MutableStateFlow(LobbyState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            sessionManager.state.transformLatest { emit(it.user?.id) }.stateIn(this).collect { id ->
                id?.let {
                    _state.value = _state.value.copy(userId = id.toString())
                }
            }
        }
        viewModelScope.launch {
            retrieveFriends()
            retrieveRequestedFriends()
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
                            retrieveFriends()
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

    private suspend fun retrieveFriends() {
        when (val result = retrieveFriendsUseCase.invoke(
            userId = sessionManager.state.value.user?.id.toString(),
            firebaseUid = sessionManager.state.value.token ?: return
        )) {
            is GFResult.Error -> Unit // handle error state
            is GFResult.Success -> _state.value = _state.value.copy(friendList = result.data)
        }
    }

    private suspend fun retrieveRequestedFriends() {
        when (val result = retrieveRequestedFriendsUseCase.invoke(
            userId = sessionManager.state.value.user?.id.toString(),
            firebaseUid = sessionManager.state.value.token ?: return
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
                    sessionManager.state.value.token?.let {
                        when (val result = searchUserUseCase.invoke(
                            username = debouncedText,
                            firebaseUid = it
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
}

