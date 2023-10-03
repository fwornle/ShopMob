package com.tanfra.shopmob.app.utils

import android.os.Build

// Android version check for "O" (Oreo)
fun ifSupportsOreo(oreoNotSupported: () -> Unit = {}, oreoSupported: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        oreoSupported()
    } else {
        oreoNotSupported()
    }
}
