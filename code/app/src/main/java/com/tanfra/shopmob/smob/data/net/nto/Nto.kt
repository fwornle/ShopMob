package com.tanfra.shopmob.smob.data.net.nto

import com.tanfra.shopmob.smob.data.types.ItemStatus
import kotlinx.serialization.Serializable


/**
 * Sealed interface of all NTO types - generic part of the i/f
 */
@Serializable
sealed interface Nto {
    val id: String
    val status: ItemStatus
    val position: Long
}