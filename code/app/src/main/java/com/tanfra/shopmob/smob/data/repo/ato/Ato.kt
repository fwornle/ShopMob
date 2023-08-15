package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.types.ItemStatus

/**
 * Sealed interface of all ATO types - generic part of the i/f
 */
sealed interface Ato {
    val id: String
    var status: ItemStatus
    var position: Long
}