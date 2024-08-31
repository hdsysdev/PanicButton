package com.hddev.panic_button.util

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Network

actual class InternetBlocker(private val connectivityManager: ConnectivityManager) {
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            connectivityManager.bindProcessToNetwork(null)
        }
    }

    actual fun blockInternet() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    actual fun unblockInternet() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
        connectivityManager.bindProcessToNetwork(null)
    }
}