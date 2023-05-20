package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.SmobItemId
import com.tanfra.shopmob.smob.data.types.SmobItemPosition

/**
 * Sealed interface of all ATO types - generic part of the i/f
 */
sealed interface Ato {
    val itemId: SmobItemId
    var itemStatus: ItemStatus
    var itemPosition: SmobItemPosition
}