package com.project.goldfish.screen.lobby

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.goldfish.logEvent
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun UsernameRoute(
    onNavigate: (String) -> Unit,
    viewModel: UsernameViewModel = koinViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.onJoinChat.collectLatest { request ->
            if(request.username.isNotBlank() && request.participant.isNotBlank()) {
                logEvent(request.username + " " + request.participant)
                onNavigate("chat_screen/${request.username}/${request.participant}")
            }
        }
    }
    val state by viewModel.state.collectAsState()
    UsernameScreen(
        onEvent = viewModel::onEvent,
        state = state
    )
}
@Composable
private fun UsernameScreen(
    state: UsernameState,
    onEvent: (UsernameEvent) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            TextField(
                value = state.participant,
                onValueChange = { text -> onEvent(UsernameEvent.OnParticipantChange(text)) },
                placeholder = {
                    Text(text = "Enter a participant id...")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Button(onClick = {onEvent(UsernameEvent.OnJoinClick) }) {
                Text(text = "Join")
            }
        }
    }
}