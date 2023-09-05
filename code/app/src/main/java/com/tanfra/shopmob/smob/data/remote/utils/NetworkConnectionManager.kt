package com.tanfra.shopmob.smob.data.remote.utils

import kotlinx.coroutines.flow.StateFlow

// ref: https://medium.com/@veniamin.vynohradov/monitoring-internet-connection-state-in-android-da7ad915b5e5
interface NetworkConnectionManager {
    /**
     * Emits [Boolean] value when the current network becomes available or unavailable.
     */
    val isNetworkConnectedFlow: StateFlow<Boolean>

    val isNetworkConnected: Boolean

    fun startListenNetworkState()

    fun stopListenNetworkState()
}