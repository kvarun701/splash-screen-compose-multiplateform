package com.ganesh.splashscreen

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview

enum class ScreenState {
    Splash,
    Home
}

@Composable
@Preview
fun App() {
    TerraTheme {
        var currentScreen by remember { mutableStateOf(ScreenState.Splash) }

        Crossfade(
            targetState = currentScreen,
            animationSpec = tween(durationMillis = 800)
        ) { screen ->
            when (screen) {
                ScreenState.Splash -> {
                    SplashScreen(
                        onSplashFinished = {
                            currentScreen = ScreenState.Home
                        }
                    )
                }
                ScreenState.Home -> {
                    HomeScreen()
                }
            }
        }
    }
}