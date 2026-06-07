package com.ganesh.splashscreen

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform