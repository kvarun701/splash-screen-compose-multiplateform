package com.ganesh.splashscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay

sealed interface Screen {
    data object Splash : Screen
    data object Login : Screen
    data object Home : Screen
}

@Composable
@Preview
fun App() {
    TerraTheme {
        val backStack = remember { mutableStateListOf<Screen>(Screen.Splash) }

        NavDisplay(
            backStack = backStack,
            entryProvider = { screen: Screen ->
                NavEntry(key = screen) { _ ->
                    when (screen) {
                        Screen.Splash -> {
                            SplashScreen(
                                onSplashFinished = {
                                    backStack.clear()
                                    backStack.add(Screen.Login)
                                }
                            )
                        }
                        Screen.Login -> {
                            LoginScreen(
                                onLoginSuccess = {
                                    backStack.clear()
                                    backStack.add(Screen.Home)
                                }
                            )
                        }
                        Screen.Home -> {
                            HomeScreen()
                        }
                    }
                }
            }
        )
    }
}
