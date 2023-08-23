package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.types.ItemStatus
import kotlinx.serialization.Serializable

/**
 * Sealed interface of all ATO types - generic part of the i/f
 */
@Serializable
sealed interface Ato {
    val id: String
    var status: ItemStatus
    var position: Long
}