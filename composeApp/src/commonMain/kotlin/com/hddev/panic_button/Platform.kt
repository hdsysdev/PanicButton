package com.hddev.panic_button

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform