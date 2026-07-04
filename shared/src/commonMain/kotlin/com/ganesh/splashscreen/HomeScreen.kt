package com.ganesh.splashscreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalInspectionMode // 1. Added import for Preview mode check
import com.ganesh.composepref.KeyValueStorage
import com.ganesh.composepref.InMemoryKeyValueStorage
import splashscreen.shared.generated.resources.Res
import splashscreen.shared.generated.resources.landscape_bg


@Composable
@Preview(showBackground = true)
fun HomeScreen(
    storage: KeyValueStorage = remember { InMemoryKeyValueStorage() },
    onLogout: () -> Unit = {}
) {
    val username = remember { storage.getString("username", defaultValue = "Eco Explorer") ?: "Eco Explorer" }
    val displayName = if (username.isBlank()) "Eco Explorer" else username
    TerraTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(WarmCream)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .safeContentPadding()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 1. Header Section
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    HeaderSection(username = displayName, onLogoutClick = onLogout)
                }

                // 2. Hero Card Section
                item {
                    HeroCard()
                }

                // 3. Section Title
                item {
                    Text(
                        text = "Popular Adventures",
                        color = DarkSageBrown,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                }

                // 4. Interactive Trail Grid/List
                item {
                    TrailItem(
                        title = "Silent Pine Path",
                        duration = "2.5 Hours",
                        difficulty = "Easy",
                        initialLikes = 42,
                        accentColor = DarkSageGreen
                    )
                }

                item {
                    TrailItem(
                        title = "Terracotta Peaks",
                        duration = "4.0 Hours",
                        difficulty = "Moderate",
                        initialLikes = 128,
                        accentColor = Terracotta
                    )
                }

                item {
                    TrailItem(
                        title = "Golden River Valley",
                        duration = "1.5 Hours",
                        difficulty = "Easy",
                        initialLikes = 95,
                        accentColor = SunOrange
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun HeaderSection(username: String, onLogoutClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Welcome back,",
                color = WarmGrey,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = username,
                color = DarkSageBrown,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Elegant Logout Icon Button
            IconButton(
                onClick = onLogoutClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SoftBeige.copy(alpha = 0.8f))
            ) {
                Canvas(modifier = Modifier.size(20.dp)) {
                    val w = size.width
                    val h = size.height
                    
                    // Draw a door/bracket line on the left
                    val bracketPath = Path().apply {
                        moveTo(w * 0.65f, h * 0.2f)
                        lineTo(w * 0.3f, h * 0.2f)
                        lineTo(w * 0.3f, h * 0.8f)
                        lineTo(w * 0.65f, h * 0.8f)
                    }
                    drawPath(
                        path = bracketPath,
                        color = Terracotta,
                        style = Stroke(
                            width = 2.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                    
                    // Draw the arrow pointing right/out
                    val arrowLine = Path().apply {
                        moveTo(w * 0.45f, h * 0.5f)
                        lineTo(w * 0.8f, h * 0.5f)
                        // Arrow head
                        moveTo(w * 0.65f, h * 0.35f)
                        lineTo(w * 0.8f, h * 0.5f)
                        lineTo(w * 0.65f, h * 0.65f)
                    }
                    drawPath(
                        path = arrowLine,
                        color = Terracotta,
                        style = Stroke(
                            width = 2.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }

            // Custom Avatar representing a sun/mountain
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Terracotta),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(28.dp)) {
                    drawCircle(color = SunOrange, radius = size.width * 0.4f)
                }
            }
        }
    }
}

@Composable
fun HeroCard() {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SoftBeige),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .fillMaxWidth()
                    .height(160.dp),
                contentAlignment = Alignment.Center
            ) {
                // 2. Wrap image loading with inspection mode check
                if (LocalInspectionMode.current) {
                    // Safe placeholder background during Preview mode to avoid crash
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DarkSageGreen)
                    )
                } else {
                    // Loads standard image at runtime on device
                    Image(
                        painter = painterResource(Res.drawable.landscape_bg),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Semi-transparent overlay to keep text readable
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f))
                )

                Text(
                    text = "Weekly Inspiration",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Discover the sand mountains & canyon pathways this weekend. Experience the golden sunset walk.",
                color = DarkSageBrown.copy(alpha = 0.8f),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("View Guide", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TrailItem(
    title: String,
    duration: String,
    difficulty: String,
    initialLikes: Int,
    accentColor: Color
) {
    var isLiked by remember { mutableStateOf(false) }
    var likesCount by remember { mutableStateOf(initialLikes) }

    // Scale animation when heart is clicked
    var buttonPressed by remember { mutableStateOf(false) }
    val heartScale by animateFloatAsState(
        targetValue = if (buttonPressed) 1.4f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium),
        finishedListener = { buttonPressed = false }
    )

    val cardBgColor by animateColorAsState(
        targetValue = if (isLiked) SoftBeige else SoftBeige.copy(alpha = 0.5f),
        animationSpec = tween(300)
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Colored dot representing the trail accent
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = title,
                        color = DarkSageBrown,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$duration • $difficulty",
                        color = WarmGrey,
                        fontSize = 12.sp
                    )
                }
            }

            // Interactive Animated Like Button (Hearts drawn using vector Canvas)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        isLiked = !isLiked
                        likesCount = if (isLiked) initialLikes + 1 else initialLikes
                        buttonPressed = true
                    }
                    .padding(8.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .size(20.dp)
                        .scale(heartScale)
                ) {
                    val w = size.width
                    val h = size.height

                    // Draw a custom heart path
                    val heartPath = Path().apply {
                        moveTo(w * 0.5f, h * 0.25f)
                        cubicTo(w * 0.5f, h * 0.2f, w * 0.25f, 0f, w * 0.1f, h * 0.25f)
                        cubicTo(-w * 0.1f, h * 0.55f, w * 0.25f, h * 0.75f, w * 0.5f, h * 0.95f)
                        cubicTo(w * 0.75f, h * 0.75f, w * 1.1f, h * 0.55f, w * 0.9f, h * 0.25f)
                        cubicTo(w * 0.75f, 0f, w * 0.5f, h * 0.2f, w * 0.5f, h * 0.25f)
                    }
                    drawPath(
                        path = heartPath,
                        color = if (isLiked) Terracotta else DarkSageBrown.copy(alpha = 0.4f)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = likesCount.toString(),
                    color = DarkSageBrown,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}