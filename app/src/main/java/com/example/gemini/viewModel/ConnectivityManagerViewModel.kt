package com.example.gemini.viewModel

import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel


// A view Model for recording network activity...
internal class ConnectivityManagerViewModel(
    private val connectivityManager: ConnectivityManager
) : ViewModel() {

    var networkState = mutableStateOf(true)

    private val networkCallback: NetworkCallback = object : NetworkCallback() {

        //What to do on Available Callback...
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            networkState.value = true
        }

        //What to do on Lost Callback...
        override fun onLost(network: Network) {
            super.onLost(network)
            networkState.value = false
        }
    }

    //Registering Network Callback...
    fun registerNetworkCallback() {
        connectivityManager.registerDefaultNetworkCallback(
            networkCallback
        )
    }

    //UnRegistering Network Callback...
    fun unRegisterNetworkCallback() {

        connectivityManager.unregisterNetworkCallback(networkCallback)

    }


}


