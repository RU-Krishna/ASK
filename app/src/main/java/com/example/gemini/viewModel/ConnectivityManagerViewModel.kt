package com.example.gemini.viewModel

import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

internal class ConnectivityManagerViewModel(
    private val connectivityManager: ConnectivityManager
) : ViewModel() {

    var networkState = mutableStateOf(true)

    private val networkCallback: NetworkCallback = object : NetworkCallback() {

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            networkState.value = true
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            networkState.value = false
        }
    }

    fun registerNetworkCallback() {
        connectivityManager.registerDefaultNetworkCallback(
            networkCallback
        )
    }

    fun unRegisterNetworkCallback() {

        connectivityManager.unregisterNetworkCallback(networkCallback)

    }


}


