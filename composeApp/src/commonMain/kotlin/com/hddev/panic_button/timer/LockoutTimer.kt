package com.hddev.panic_button.timer

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock

class LockoutTimer {
    private val _remainingTime = MutableStateFlow(0L)
    val remainingTime: StateFlow<Long> = _remainingTime.asStateFlow()

    private var timerJob: Job? = null

    fun startTimer(durationMinutes: Int) {
        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            val endTime = Clock.System.now().toEpochMilliseconds() + durationMinutes * 60 * 1000
            while (isActive) {
                val remaining = endTime - Clock.System.now().toEpochMilliseconds()
                if (remaining <= 0) {
                    _remainingTime.value = 0
                    break
                }
                _remainingTime.value = remaining
                delay(1000)
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _remainingTime.value = 0
    }
}