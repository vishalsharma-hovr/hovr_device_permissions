package com.hovr.devicepermissions.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build

internal class NetworkCallbackHandler(
    private val onAvailable: () -> Unit,
    private val onLost: () -> Unit,
) {
    val callback: ConnectivityManager.NetworkCallback =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                onAvailable()
            }

            override fun onLost(network: Network) {
                onLost()
            }

            override fun onCapabilitiesChanged(
                network: Network,
                capabilities: NetworkCapabilities,
            ) {
                if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    onAvailable()
                } else {
                    onLost()
                }
            }
        }

    fun register(manager: ConnectivityManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            manager.registerDefaultNetworkCallback(callback)
        } else {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            manager.registerNetworkCallback(request, callback)
        }
    }

    fun unregister(manager: ConnectivityManager) {
        manager.unregisterNetworkCallback(callback)
    }
}

internal fun ConnectivityManager.hasInternet(): Boolean {
    val network = activeNetwork ?: return false
    val capabilities = getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
