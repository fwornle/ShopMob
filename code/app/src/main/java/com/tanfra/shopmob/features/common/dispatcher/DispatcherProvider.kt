package com.tanfra.shopmob.features.common.dispatcher

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherProvider {
    val ui: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}