package com.tanfra.shopmob.smob.data.remote.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

// ref: https://medium.com/@veniamin.vynohradov/monitoring-internet-connection-state-in-android-da7ad915b5e5
class NetworkConnectionManagerImpl(
    context: Context,
    coroutineScope: CoroutineScope
) : NetworkConnectionManager {

    // fetch ConnectivityManager (system service)
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // define behavior on network changes (see private inner class 'NetworkCallback', below)
    private val networkCallback = NetworkCallback()

    // network state (implemented as StateFlow)
    private val _currentNetwork = MutableStateFlow(provideDefaultCurrentNetwork())


    /*
     * implementation of interface 'NetworkConnectionManager'
     */

    // aggregated network connection status (mapped to simple Boolean from underlying data class)
    // --> implemented as StateFlow, ie. needs to be collected in the relevant lifeCycle of the app
    //     (= without this, the connection status will always be reported as false!)
    override val isNetworkConnectedFlow: StateFlow<Boolean> =
        _currentNetwork
//            .map { true } // work without network checking (in-flight, etc.)
            .map { it.isConnected() }
            .stateIn(
                scope = coroutineScope,
                // SharingStarted.Eagerly!! (collect flow right away or the network will seem
                // unavailable until first collection is triggered... e.g. in BindingAdapter)
                started = SharingStarted.Eagerly,
                initialValue = _currentNetwork.value.isConnected()
            )

    // read property for network connection status StateFlow value
    // --> this is where the end user sees the current network connection status
    override val isNetworkConnected: Boolean
        get() = isNetworkConnectedFlow.value

    // start listening to changes of the network connection status StateFlow variable
    // (= activation of the network connection monitoring StateFlow)
    override fun startListenNetworkState() {
        if (_currentNetwork.value.isListening) {
            return
        }

        // Reset state before start listening
        _currentNetwork.update {
            provideDefaultCurrentNetwork()
                .copy(isListening = true)
        }

        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    // stop listening to changes of the network connection status StateFlow variable
    // (= deactivation of the network connection monitoring StateFlow)
    override fun stopListenNetworkState() {
        if (!_currentNetwork.value.isListening) {
            return
        }

        _currentNetwork.update {
            it.copy(isListening = false)
        }

        connectivityManager.unregisterNetworkCallback(networkCallback)
    }


    /*
     * define network state update callbacks
     * --> instantiated above, as 'val networkCallback', reg. in start/stopListenNetworkState()
     */
    private inner class NetworkCallback : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            _currentNetwork.update {
                it.copy(isAvailable = true)
            }
        }

        override fun onLost(network: Network) {
            _currentNetwork.update {
                it.copy(
                    isAvailable = false,
                    networkCapabilities = null
                )
            }
        }

        override fun onUnavailable() {
            _currentNetwork.update {
                it.copy(
                    isAvailable = false,
                    networkCapabilities = null
                )
            }
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            _currentNetwork.update {
                it.copy(networkCapabilities = networkCapabilities)
            }
        }

        override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
            _currentNetwork.update {
                it.copy(isBlocked = blocked)
            }
        }

    }  // (inner private) class NetworkCallback


    /**
     * On Android 9, [ConnectivityManager.NetworkCallback.onBlockedStatusChanged] is not called when
     * we call the [ConnectivityManager.registerDefaultNetworkCallback] function.
     * Hence we assume that the network is unblocked by default.
     */
    private fun provideDefaultCurrentNetwork(): CurrentNetwork {
        return CurrentNetwork(
            isListening = false,
            networkCapabilities = null,
            isAvailable = false,
            isBlocked = false
        )
    }

    private data class CurrentNetwork(
        val isListening: Boolean,
        val networkCapabilities: NetworkCapabilities?,
        val isAvailable: Boolean,
        val isBlocked: Boolean
    )

    private fun CurrentNetwork.isConnected(): Boolean {
        // since we don't know the network state if NetworkCallback is not registered, we assume
        // that it's disconnected.
        return isListening &&
                isAvailable &&
                !isBlocked &&
                networkCapabilities.isNetworkCapabilitiesValid()
    }

    private fun NetworkCapabilities?.isNetworkCapabilitiesValid(): Boolean = when {
        this == null -> false
        hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                (hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_VPN) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) -> true
        else -> false
    }

}