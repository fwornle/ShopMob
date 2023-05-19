package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus

/**
 * Sealed interface of all ATO types - generic part of the i/f
 */
sealed interface Ato {
    val itemId: String
    var itemStatus: SmobItemStatus
    var itemPosition: Long
}