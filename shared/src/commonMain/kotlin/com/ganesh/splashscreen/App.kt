package com.ganesh.splashscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.ganesh.composepref.KeyValueStorage
import com.ganesh.composepref.InMemoryKeyValueStorage
import androidx.compose.ui.platform.LocalInspectionMode

sealed interface Screen {
    data object Splash : Screen
    data object Login : Screen
    data object Register : Screen
    data object Home : Screen
}

@Composable
@Preview
fun App(storage: KeyValueStorage = remember { InMemoryKeyValueStorage() }) {
    TerraTheme {
        val backStack = remember { mutableStateListOf<Screen>(Screen.Splash) }
        val isPreview = LocalInspectionMode.current
        val databaseHelper = remember {
            if (isPreview) {
                null
            } else {
                try {
                    DatabaseHelper(DatabaseDriverFactory().createDriver())
                } catch (e: Exception) {
                    println("DatabaseHelper init failed: ${e.message}")
                    null
                }
            }
        }

        NavDisplay(
            backStack = backStack,
            entryProvider = { screen: Screen ->
                NavEntry(key = screen) { _ ->
                    when (screen) {
                        Screen.Splash -> {
                            SplashScreen(
                                onSplashFinished = {
                                    val savedUser = storage.getString("username", "")
                                    backStack.clear()
                                    if (!savedUser.isNullOrBlank()) {
                                        backStack.add(Screen.Home)
                                    } else {
                                        backStack.add(Screen.Login)
                                    }
                                }
                            )
                        }
                        Screen.Login -> {
                            LoginScreen(
                                onLoginSuccess = { username ->
                                    storage.putString("username", username)
                                    backStack.clear()
                                    backStack.add(Screen.Home)
                                },
                                onNavigateToRegister = {
                                    backStack.add(Screen.Register)
                                },
                                databaseHelper = databaseHelper
                            )
                        }
                        Screen.Register -> {
                            RegisterScreen(
                                onRegisterSuccess = { username ->
                                    storage.putString("username", username)
                                    backStack.clear()
                                    backStack.add(Screen.Home)
                                },
                                onNavigateToLogin = {
                                    backStack.remove(Screen.Register)
                                },
                                databaseHelper = databaseHelper
                            )
                        }
                        Screen.Home -> {
                            HomeScreen(
                                storage = storage,
                                onLogout = {
                                    storage.clear()
                                    backStack.clear()
                                    backStack.add(Screen.Login)
                                }
                            )
                        }
                    }
                }
            }
        )
    }
}
