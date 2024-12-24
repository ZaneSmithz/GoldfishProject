package com.project.goldfish

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform