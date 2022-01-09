package com.tanfra.shopmob.smob.ui.details

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf

/**
 * Intent creating function (inline, reified --> can be used with any type we need to communicate.
 * Formulated as extension function of Context. Several Pairs of <key, value> can be provided.
 */
inline fun <reified T : Activity> Context.createIntent(vararg args: Pair<String, Any>) : Intent {
    val intent = Intent(this, T::class.java)
    intent.putExtras(bundleOf(*args))
    return intent
}