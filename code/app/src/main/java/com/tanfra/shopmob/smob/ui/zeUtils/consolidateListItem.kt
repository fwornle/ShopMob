package com.tanfra.shopmob.smob.ui.zeUtils

import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.types.ItemStatus
import kotlin.math.roundToInt

// recompute status & completion rate from linked list items
fun consolidateListItem(item: SmobListATO): SmobListATO {

    // consolidate smobList...
    val valItems = item.items.filter { itm -> itm.status != ItemStatus.DELETED }
    val nValItems = valItems.size

    // ... status
    val aggListStatus =
        valItems.fold(0) { sum, daItem -> sum + daItem.status.ordinal }
    item.status = when (aggListStatus) {
        in 0..nValItems -> ItemStatus.OPEN
        nValItems * ItemStatus.DONE.ordinal -> ItemStatus.DONE
        else -> ItemStatus.IN_PROGRESS
    }

    // ... completion rate (= nDONE/nTOTAL)
    item.lifecycle.completion = when(nValItems) {
        0 -> 0.0
        else -> {
            val doneItems = valItems.filter { daItem -> daItem.status == ItemStatus.DONE }.size
            (100.0 * doneItems / nValItems).roundToInt().toDouble()
        }
    }

    // return adjusted item
    return item

}  // consolidateListItem
