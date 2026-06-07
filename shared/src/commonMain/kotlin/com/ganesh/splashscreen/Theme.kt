package com.ganesh.splashscreen

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Color palette inspired by the warm earthy mountain artwork
val WarmCream = Color(0xFFF4EAE1)
val Terracotta = Color(0xFFC26D5C)
val Sand = Color(0xFFD9A07D)
val SunOrange = Color(0xFFE78F66)
val DarkSageBrown = Color(0xFF3C3530)
val SoftBeige = Color(0xFFE8DFD8)
val WarmGrey = Color(0xFF8E837D)
val DarkSageGreen = Color(0xFF5A665A)

private val EarthyColorScheme = lightColorScheme(
    primary = Terracotta,
    secondary = Sand,
    tertiary = SunOrange,
    background = WarmCream,
    surface = SoftBeige,
    onPrimary = Color.White,
    onSecondary = DarkSageBrown,
    onBackground = DarkSageBrown,
    onSurface = DarkSageBrown
)

@Composable
fun TerraTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EarthyColorScheme,
        content = content
    )
}
