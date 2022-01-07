package com.tanfra.shopmob.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

// way to combine two Kotlin StateFlow variables
// ref: https://stackoverflow.com/questions/65444049/combine-two-state-flows-into-new-state-flow
fun <T1, T2, R> combineState(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    scope: CoroutineScope,
    sharingStarted: SharingStarted = SharingStarted.Eagerly,
    transform: (T1, T2) -> R
): StateFlow<R> = combine(flow1, flow2) {
            o1, o2 -> transform.invoke(o1, o2)
    }.stateIn(scope, sharingStarted, transform.invoke(flow1.value, flow2.value))
