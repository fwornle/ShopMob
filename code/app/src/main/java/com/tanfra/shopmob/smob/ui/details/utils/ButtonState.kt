package com.tanfra.shopmob.smob.ui.details.utils

// adopted from Kotlin/Android course
sealed class ButtonState {
    object Running : ButtonState()
    object Stopped : ButtonState()
    object Active : ButtonState()

    // state "machine"
    fun next() = when (this) {
        Active -> Running
        Running -> Stopped
        Stopped -> Active
    }
}