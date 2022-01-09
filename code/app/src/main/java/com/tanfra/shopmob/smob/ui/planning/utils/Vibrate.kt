package com.tanfra.shopmob.smob.ui.planning.utils

import android.os.VibrationEffect
import android.os.Vibrator
import com.tanfra.shopmob.utils.ifSupportsOreo

// vibrate for a bunch of milliseconds
fun vibrateDevice(vib: Vibrator, durationInMs: Long) {
    ifSupportsOreo(oreoNotSupported = {@Suppress("DEPRECATION") vib.vibrate(durationInMs)}) {
        vib.vibrate(VibrationEffect.createOneShot(durationInMs, VibrationEffect.DEFAULT_AMPLITUDE));
    }
}
