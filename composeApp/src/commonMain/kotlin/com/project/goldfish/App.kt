package com.project.goldfish

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.project.goldfish.screen.ChatScreen
import com.project.goldfish.screen.UsernameScreen
import org.koin.compose.KoinContext

@Composable
fun App() {
    MaterialTheme {
        KoinContext {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = "username_screen"
            ) {
                composable("username_screen") {
                    UsernameScreen(onNavigate = navController::navigate)
                }
                composable("chat_screen/{username}",
                    arguments = listOf(
                        navArgument(name = "username") {
                            type = NavType.StringType
                            nullable = true
                        }
                    )) {
                    val username = it.arguments?.getString("username")
                    ChatScreen(username = username)
                }
            }
        }
    }
}