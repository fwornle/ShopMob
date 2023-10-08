package com.tanfra.shopmob.features.common.monitor

import kotlinx.coroutines.flow.Flow

interface ConnectivityMonitor {
    val statusFlow: Flow<ConnectivityStatus>
}

enum class ConnectivityStatus {
    AVAILABLE, UNAVAILABLE, LOST
}