package com.project.goldfish.screen.lobby

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.goldfish.logEvent
import com.project.goldfish.model.event.LobbyEvent
import com.project.goldfish.model.state.LobbyState
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun LobbyRoute(
    onNavigate: (String) -> Unit,
    viewModel: LobbyViewModel = koinViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.onJoinChat.collectLatest { request ->
            if (request.userId.isNotBlank() && request.participantId.isNotBlank()) {
                logEvent("FROM LOBBY LAUCNCH = ${request.userId}  ${request.participantId}")
                onNavigate("chat_screen/${request.userId}/${request.participantId}")
            }
        }
    }
    val state by viewModel.state.collectAsState()
    LobbyScreen(
        onEvent = viewModel::onEvent,
        state = state
    )
}

@Composable
private fun LobbyScreen(
    state: LobbyState,
    onEvent: (LobbyEvent) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize().systemBarsPadding().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.End
        ) {
            TextField(
                value = state.friendIdText,
                onValueChange = { text -> onEvent(LobbyEvent.OnFriendTextChange(text)) },
                placeholder = {
                    Text(text = "Add Friend...")
                },
                modifier = Modifier.fillMaxWidth()
            )
            LazyColumn {
                item {
                    Text(text = "Friends Search", color = Color.Red, fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))
                }
                items(state.friendSearchList) { friend ->
                    Row(modifier = Modifier.fillMaxWidth().height(50.dp).clip(
                        RoundedCornerShape(9.dp)).background(Color.Blue).clickable {
                        onEvent(LobbyEvent.OnAddFriend(friend.id.toString()))
                    },
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Text(text = friend.username, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                }

            }
            Spacer(Modifier.height(8.dp))
        }
        LazyColumn {
            item {
                Text(text = "Current Match!", color = Color.Red, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
            }
            item {
                Row(modifier = Modifier.fillMaxWidth().height(50.dp).clip(
                    RoundedCornerShape(9.dp)).background(Color.Red) .clickable {
                    onEvent(LobbyEvent.OnJoinClick(state.matchedUser?.id.toString()))
                },
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Text(text = state.matchedUser?.username ?: "Error!", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
            }
            item {
                Text(text = "Friends", color = Color.Red, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
            }
            items(state.friendList) { friend ->
                Row(modifier = Modifier.fillMaxWidth().height(50.dp).clip(
                    RoundedCornerShape(9.dp)).background(Color.Blue) .clickable {
                    onEvent(LobbyEvent.OnJoinClick(friend.id.toString()))
                },
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Text(text = friend.username, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))

            }
            item {
                Text(text = "Friends Requests", color = Color.Red, fontSize = 16.sp)

            }
            items(state.requestedFriendList) { requestedFriend ->
                Row(modifier = Modifier.fillMaxWidth().height(50.dp).clip(
                    RoundedCornerShape(9.dp)).background(Color.Green) .clickable {
                    onEvent(LobbyEvent.OnAcceptFriend(requestedFriend.id.toString()))
                },
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Text(text = requestedFriend.username, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))

            }

        }
    }
}