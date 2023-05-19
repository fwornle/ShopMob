package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.local.utils.ItemStatus

/**
 * Sealed interface of all ATO types - generic part of the i/f
 */
sealed interface Ato {
    val itemId: String
    var itemStatus: ItemStatus
    var itemPosition: Long
}