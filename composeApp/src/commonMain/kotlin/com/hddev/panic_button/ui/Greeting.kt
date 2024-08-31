package com.hddev.panic_button.ui

import com.hddev.panic_button.getPlatform

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}