package com.ganesh.splashscreen

import androidx.compose.ui.window.ComposeUIViewController
import com.ganesh.composepref.KeyValueStorageFactory

fun MainViewController() = ComposeUIViewController {
    val storage = KeyValueStorageFactory().create("app_preferences")
    App(storage = storage)
}