package com.ganesh.splashscreen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    databaseHelper: DatabaseHelper? = null
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var animateStart by remember { mutableStateOf(false) }

    val fadeAnim by animateFloatAsState(
        targetValue = if (animateStart) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = EaseInOutQuad)
    )

    LaunchedEffect(Unit) {
        animateStart = true
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .alpha(fadeAnim)
                .offset(y = ((1f - fadeAnim) * 20).dp)
        ) {
            // Earthy logo/branding header
            Text(
                text = "T E R R A",
                color = DarkSageBrown,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Explore the path ahead.",
                color = WarmGrey,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.SansSerif
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Card container for inputs to look clean and structured
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SoftBeige.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Register",
                        color = DarkSageBrown,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = { 
                            username = it
                            errorMessage = null
                        },
                        label = { Text("Username", color = WarmGrey) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Terracotta,
                            unfocusedBorderColor = WarmGrey.copy(alpha = 0.5f),
                            focusedLabelColor = Terracotta,
                            cursorColor = Terracotta
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { 
                            email = it
                            errorMessage = null
                        },
                        label = { Text("Email", color = WarmGrey) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Terracotta,
                            unfocusedBorderColor = WarmGrey.copy(alpha = 0.5f),
                            focusedLabelColor = Terracotta,
                            cursorColor = Terracotta
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = mobile,
                        onValueChange = {
                            mobile = it
                            errorMessage = null
                        },
                        label = { Text("Mobile", color = WarmGrey) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Terracotta,
                            unfocusedBorderColor = WarmGrey.copy(alpha = 0.5f),
                            focusedLabelColor = Terracotta,
                            cursorColor = Terracotta
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            errorMessage = null
                        },
                        label = { Text("Password", color = WarmGrey) },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val image = if (passwordVisible) {
                                Icons.Filled.Visibility
                            } else {
                                Icons.Filled.VisibilityOff
                            }

                            val description = if (passwordVisible) "Hide password" else "Show password"

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = description, tint = WarmGrey)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Terracotta,
                            unfocusedBorderColor = WarmGrey.copy(alpha = 0.5f),
                            focusedLabelColor = Terracotta,
                            cursorColor = Terracotta
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = Terracotta,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (username.isBlank() || email.isBlank() || mobile.isBlank() || password.isBlank()) {
                                errorMessage = "Fields cannot be empty"
                                return@Button
                            }
                            if (databaseHelper != null) {
                                val existingUser = databaseHelper.getUser(username)
                                if (existingUser != null) {
                                    errorMessage = "Username already taken"
                                    return@Button
                                }
                                databaseHelper.insertUser(
                                    com.ganesh.splashscreen.database.User(
                                        username = username,
                                        email = email,
                                        password = password,
                                        mobile = mobile
                                    )
                                )
                                onRegisterSuccess(username)
                            } else {
                                onRegisterSuccess(username)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Register",
                            color = androidx.compose.ui.graphics.Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Already have an account? ",
                            color = WarmGrey,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                        Text(
                            text = "Login",
                            color = Terracotta,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.clickable { onNavigateToLogin() }
                        )
                    }
                }
            }
        }
    }
}
