package com.project.goldfish.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.goldfish.domain.ChatState
import com.project.goldfish.logEvent
import com.project.goldfish.screen.ChatEvent.OnSendMessage
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ChatRoute(
    userId: String?,
    participant: String?,
    viewModel: ChatViewModel = koinViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.onEvent(ChatEvent.OnConnect)
        viewModel.toastEvent.collectLatest { message ->
            logEvent(message)
        }
    }

    val state by viewModel.state.collectAsState()
    val messageText by viewModel.messageText

    ChatScreen(
        userId = userId,
        onEvent = viewModel::onEvent,
        state = state,
        messageText = messageText
    )

}
@Composable
private fun ChatScreen(
    userId: String?,
    onEvent: (ChatEvent) -> Unit,
    messageText: String,
    state: ChatState
) {

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), reverseLayout = true) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
            items(state.messages) { message ->
                logEvent(message.userId + "==" + userId)
                val isOwnMessage = message.userId == userId
                Box(
                    contentAlignment = if (isOwnMessage) Alignment.CenterEnd
                    else Alignment.CenterStart,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.width(200.dp)
                            .drawBehind {
                                val cornerRadius = 10.dp.toPx()
                                val triangleHeight = 20.dp.toPx()
                                val triangleWidth = 25.dp.toPx()
                                val trianglePath = Path().apply {
                                    if (isOwnMessage) {
                                        moveTo(size.width, size.height - cornerRadius)
                                        lineTo(size.width, size.height + triangleHeight)
                                        lineTo(
                                            size.width - triangleWidth,
                                            size.height - cornerRadius
                                        )
                                        close()
                                    } else {
                                        moveTo(0f, size.height - cornerRadius)
                                        lineTo(0f, size.height + triangleHeight)
                                        lineTo(triangleWidth, size.height - cornerRadius)
                                        close()
                                    }
                                }
                                drawPath(
                                    path = trianglePath,
                                    color = if (isOwnMessage) Color.Green else Color.DarkGray
                                )
                            }.background(
                                color = if (isOwnMessage) Color.Green else Color.DarkGray,
                                shape = RoundedCornerShape(10.dp)
                            ).padding(8.dp)
                    ) {
                        Text(
                            text = message.userId,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = message.text,
                            color = Color.White
                        )
                        Text(
                            text = message.formattedTime.toString(),
                            color = Color.White,
                            modifier = Modifier.align(Alignment.End)
                        )

                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(value = messageText,
                onValueChange = {text -> onEvent(ChatEvent.OnMessageChange(text))},
                placeholder = {
                    Text(text = "Enter a message")
                },
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { onEvent(OnSendMessage) }) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}