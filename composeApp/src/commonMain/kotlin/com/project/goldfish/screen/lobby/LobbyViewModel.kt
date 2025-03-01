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
import com.project.goldfish.domain.moments.GetAllMomentsUseCase
import com.project.goldfish.domain.moments.InsertMomentUseCase
import com.project.goldfish.logEvent
import com.project.goldfish.model.event.LobbyEvent
import com.project.goldfish.model.request.ChatRequest
import com.project.goldfish.model.state.LobbyState
import com.project.goldfish.network.moments.MomentInsertionRequest
import com.project.goldfish.util.GFResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class LobbySessionDetails(
    val userId: String = "",
    val firebaseToken: String = ""
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class LobbyViewModel(
    private val sessionManager: SessionManager,
    private val acceptFriendUseCase: AcceptFriendUseCase,
    private val addFriendUseCase: AddFriendUseCase,
    private val retrieveFriendsUseCase: RetrieveFriendsUseCase,
    private val retrieveRequestedFriendsUseCase: RetrieveRequestedFriendsUseCase,
    private val searchUserUseCase: SearchUserUseCase,
    private val retrieveMatchUseCase: RetrieveMatchUseCase,
    private val insertMomentUseCase: InsertMomentUseCase,
    private val getAllMomentsUseCase: GetAllMomentsUseCase
) : ViewModel() {
    private val _onJoinChat = MutableSharedFlow<ChatRequest>()
    val onJoinChat = _onJoinChat.asSharedFlow()

    private val _state = MutableStateFlow(LobbyState())
    val state = _state.asStateFlow()

    private var timeJob: Job? = null

    init {
        stopTimeJob()
        viewModelScope.launch {
            sessionManager.state.transformLatest {
                if (it.user != null) {
                    it.token?.let { token ->
                        emit(
                            LobbySessionDetails(
                                userId = it.user.id.toString(),
                                firebaseToken = token
                            )
                        )
                    }
                }
            }.stateIn(this).collect { sessionDetails ->
                _state.update { state ->
                    state.copy(
                        sessionDetails = sessionDetails,
                    )
                }
                retrieveFriends(
                    userId = sessionDetails.userId,
                    token = sessionDetails.firebaseToken
                )
                retrieveRequestedFriends(
                    userId = sessionDetails.userId,
                    token = sessionDetails.firebaseToken
                )
                retrieveMatch(sessionDetails)
                retrieveMomentList(sessionDetails)

            }
        }
        timeJob = viewModelScope.launch(Dispatchers.IO) {
            _state.transformLatest { emit(it.matchedUser?.createdAt) }.stateIn(this).collect {
                if (it != null) {
                    while (it > 0) {
                        val remainingTime = calculateRemainingTime(it)
                        if (remainingTime > 0) {
                            val hours = (remainingTime / (60 * 60 * 1000)).toInt().toString()
                                .padStart(2, '0')
                            val minutes = ((remainingTime % (60 * 60 * 1000)) / (60 * 1000)).toInt()
                                .toString().padStart(2, '0')
                            val format = "$hours:$minutes"
                            logEvent("hours minutes = $format")
                            _state.update { state -> state.copy(remainingTime = format) }
                            delay(60000)
                        } else {
                            _state.update { state ->
                                state.copy(remainingTime = "00:00")
                            }
                        }
                    }
                }
            }
        }
        retrieveSearchResultContent()
    }


    fun onEvent(usernameEvent: LobbyEvent) {
        when (usernameEvent) {
            is LobbyEvent.OnJoinClick -> onJoinClick(usernameEvent.friendId)
            is LobbyEvent.OnAddFriend -> {
                _state.update { state ->
                    state.copy(friendIdText = "", )
                }
                viewModelScope.launch {
                    when (val result =
                        addFriendUseCase.invoke(
                            userId = sessionManager.state.value.user?.id.toString(),
                            friendId = usernameEvent.friendId,
                            firebaseUid = sessionManager.state.value.token ?: return@launch
                        )) {
                        is GFResult.Error -> {
                            logEvent("${result.error} or Unknown Error!")
                        }

                        is GFResult.Success -> {
                            retrieveFriends(
                                userId = state.value.sessionDetails.userId,
                                token = state.value.sessionDetails.firebaseToken
                            )
                        }
                    }
                }
            }

            is LobbyEvent.OnFriendTextChange -> {
                _state.value = _state.value.copy(friendIdText = usernameEvent.text)
            }

            is LobbyEvent.OnAcceptFriend -> acceptFriend(usernameEvent.friendId)
            is LobbyEvent.OnInsertMoment -> {
                viewModelScope.launch {
                    insertMomentUseCase.invoke(
                        MomentInsertionRequest(
                            userId = state.value.sessionDetails.userId,
                            title = usernameEvent.title,
                            description = usernameEvent.description,
                            firebaseUid = state.value.sessionDetails.firebaseToken
                        )
                    )
                }
            }
        }
    }

    private suspend fun retrieveMomentList(sessionDetails: LobbySessionDetails) {
        _state.update { state ->
            state.copy(
                momentList = getAllMomentsUseCase.invoke(
                    sessionDetails.userId,
                    firebaseToken = sessionDetails.firebaseToken
                )
            )
        }
    }

    private suspend fun retrieveFriends(userId: String, token: String) {
        when (val result = retrieveFriendsUseCase.invoke(
            userId = userId,
            firebaseUid = token,
        )) {
            is GFResult.Error -> {} // TODO error Handling
            is GFResult.Success -> {
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
            when (acceptFriendUseCase.invoke(
                userId = state.value.sessionDetails.userId,
                friendId = friendId,
                firebaseUid = state.value.sessionDetails.firebaseToken
            )) {
                is GFResult.Error -> Unit // error handling
                is GFResult.Success -> {
                }
            }
        }
    }

    private suspend fun retrieveMatch(sessionDetails: LobbySessionDetails) {
        when (val result = retrieveMatchUseCase.invoke(
            userId = sessionDetails.userId,
            firebaseUid = sessionDetails.firebaseToken
        )) {
            is GFResult.Error -> {
                logEvent("result failed! ${result.error}")

            }

            is GFResult.Success -> {
                logEvent("result succeeded! ${result.data}")
                _state.update { state ->
                    state.copy(
                        matchedUser = result.data
                    )
                }
            }
        }
    }


    private fun onJoinClick(participantId: String) {
        viewModelScope.launch {
            if (_state.value.sessionDetails.userId.isNotBlank() && participantId.isNotBlank()) {
                _onJoinChat.emit(
                    ChatRequest(
                        userId = _state.value.sessionDetails.userId,
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
                        firebaseUid = state.value.sessionDetails.firebaseToken
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

    private fun calculateRemainingTime(targetTime: Long): Long {
        return targetTime.plus(2 * 24 * 60 * 60 * 1000) - Clock.System.now().toEpochMilliseconds()
    }


    private fun stopTimeJob() {
        timeJob?.cancel()
        timeJob = null
    }
}

