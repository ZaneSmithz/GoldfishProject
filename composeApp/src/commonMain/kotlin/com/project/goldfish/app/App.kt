package com.project.goldfish.app

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.project.goldfish.screen.chat.ChatRoute
import com.project.goldfish.screen.lobby.LobbyRoute
import com.project.goldfish.screen.login.LoginRoute
import com.project.goldfish.screen.registration.RegistrationScreen
import org.koin.compose.KoinContext

@Composable
fun App() {
    MaterialTheme {
        KoinContext {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = "login_screen"
            ) {
                composable("login_screen") {
                    LoginRoute(onNavigate = navController::navigate)
                }
                composable("register_screen") {
                    RegistrationScreen(onNavigate = navController::navigate)
                }
                composable("username_screen") {
                    LobbyRoute(onNavigate = navController::navigate)
                }
                composable("chat_screen/{userId}/{participantId}",
                    arguments = listOf(
                        navArgument(name = "userId") {
                            type = NavType.StringType
                            nullable = true
                        },
                        navArgument(name = "participantId") {
                            type = NavType.StringType
                            nullable = true
                        }
                    )) {
                    val username = it.arguments?.getString("userId")
                    ChatRoute(userId = username)
                }
            }
        }
    }
}