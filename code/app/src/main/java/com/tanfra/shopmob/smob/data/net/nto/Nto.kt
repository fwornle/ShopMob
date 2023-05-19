package com.tanfra.shopmob.smob.data.net.nto

import com.tanfra.shopmob.smob.data.local.utils.ItemStatus

/**
 * Sealed interface of all NTO types - generic part of the i/f
 */
sealed interface Nto {
    val id: String
    var itemStatus: ItemStatus
    var itemPosition: Long
}