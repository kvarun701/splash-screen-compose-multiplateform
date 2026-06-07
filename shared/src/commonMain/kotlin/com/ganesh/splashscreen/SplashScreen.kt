package com.ganesh.splashscreen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import splashscreen.shared.generated.resources.Res
import splashscreen.shared.generated.resources.landscape_bg

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var startAnim by remember { mutableStateOf(false) }

    // Staggered Animations
    // 1. The artwork card fades in and scales up smoothly
    val cardProgress by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(durationMillis = 1600, easing = EaseOutCubic)
    )

    // 2. The title and subtitle text fades in and slides up
    val textProgress by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(durationMillis = 1200, delayMillis = 600, easing = EaseOutQuad)
    )

    // Trigger animations on start, and navigate away after completion
    LaunchedEffect(Unit) {
        startAnim = true
        delay(2800) // Stays visible during transitions, then completes
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCream),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Main Landscape Image Card
            Box(
                modifier = Modifier
                    .width(280.dp)
                    .height(380.dp)
                    .alpha(cardProgress)
                    .scale(0.85f + (cardProgress * 0.15f))
                    .offset(y = ((1f - cardProgress) * 30).dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(SoftBeige),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.landscape_bg),
                    contentDescription = "Landscape",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Typographic Title & Subtitle with slide-up and fade-in
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .alpha(textProgress)
                    .offset(y = ((1f - textProgress) * 15).dp)
            ) {
                Text(
                    text = "T E R R A",
                    color = DarkSageBrown,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = "A E S T H E T I C  L A N D S C A P E S",
                    color = WarmGrey,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }
    }
}
