package com.tanfra.shopmob.app.utils

import android.content.Intent
import android.os.Build
import java.io.Serializable

// using standard "java" serialization to
@Suppress("DEPRECATION", "UNCHECKED_CAST")
fun <T : Serializable?> Intent.getSerializable(key: String, m_class: Class<T>): T {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        this.getSerializableExtra(key, m_class)!!
    else
        this.getSerializableExtra(key) as T
}