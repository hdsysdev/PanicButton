package com.hddev.panic_button.service

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.net.ConnectivityManager
import com.hddev.panic_button.timer.LockoutTimer
import com.hddev.panic_button.util.InternetBlocker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel

actual class PanicButtonTileService : TileService() {
    private val lockoutTimer = LockoutTimer()
    private val internetBlocker by lazy { InternetBlocker(getSystemService(ConnectivityManager::class.java)) }
    private val serviceScope = CoroutineScope(Dispatchers.Default)

    override fun onClick() {
        super.onClick()
        if (qsTile.state == Tile.STATE_INACTIVE) {
            activatePanicMode()
        } else {
            deactivatePanicMode()
        }
        qsTile.updateTile()
    }

    private fun activatePanicMode() {
        qsTile.state = Tile.STATE_ACTIVE
        serviceScope.launch {
            lockoutTimer.startTimer(10)
            internetBlocker
            lockoutTimer.remainingTime.collect { remaining ->
                if (remaining == 0L) {
                    deactivatePanicMode()
                }
            }
        }
    }

    private fun deactivatePanicMode() {
        qsTile.state = Tile.STATE_INACTIVE
        serviceScope.launch {
            lockoutTimer.stopTimer()
            internetBlocker.unblockInternet()
        }
        qsTile.updateTile()
    }

    override fun onStartListening() {
        super.onStartListening()
        qsTile.state = if (lockoutTimer.remainingTime.value > 0) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}