package com.ganesh.splashscreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ganesh.composepref.KeyValueStorageFactory
import com.ganesh.composepref.InMemoryKeyValueStorage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val storage = KeyValueStorageFactory(applicationContext).create("app_preferences")

        setContent {
            App(storage = storage)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(storage = InMemoryKeyValueStorage())
}