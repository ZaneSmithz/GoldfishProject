package com.project.goldfish.screen.registration

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.goldfish.logEvent
import com.project.goldfish.model.event.RegistrationEvent
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegistrationScreen(
    onNavigate: (String) -> Unit,
    viewModel: RegistrationViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.sessionState.collect { session ->
            if (session.user != null) {
                logEvent(session.user.firebaseUid)
                onNavigate("username_screen")
            }
        }
    }
    RegistrationScreen(viewModel::onEvent)
}

@Composable
fun RegistrationScreen(
    onEvent: (RegistrationEvent) -> Unit
) {
    val scope = rememberCoroutineScope()
    val auth = remember { Firebase.auth }
    var firebaseUser: FirebaseUser? by remember { mutableStateOf(null) }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(firebaseUser) {
        if (firebaseUser != null) {
            firebaseUser?.let { user ->
                user.getIdToken(true)?.let { token ->
                    logEvent("TOKEN = $token")
                    onEvent(RegistrationEvent.OnRegisterClick(firebaseUid = token, username = username))
                }
            }
        }
    }
    if (firebaseUser == null) {
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
                    value = email,
                    onValueChange = { email = it },
                    placeholder = {
                        Text(text = "Email")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = {
                        Text(text = "Username")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {
                        Text(text = "Password")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    scope.launch {
                        auth.createUserWithEmailAndPassword(
                            email = email,
                            password = password
                        )
                        firebaseUser = auth.currentUser
                    }
                }
                ) {
                    Text(text = "Join")
                }
            }
        }
    }
}