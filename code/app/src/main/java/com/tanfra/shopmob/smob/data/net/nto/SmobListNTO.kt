package com.tanfra.shopmob.smob.data.net.nto

import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.SmobGroupItem
import com.tanfra.shopmob.smob.data.types.SmobListItem
import com.tanfra.shopmob.smob.data.types.SmobListLifecycle

// network data type
data class SmobListNTO(
    override val itemId: String,
    override var itemStatus: ItemStatus,
    override var itemPosition: Long,
    var name: String,
    var description: String?,
    var items: List<SmobListItem>,
    var groups: List<SmobGroupItem>,
    var lifecycle: SmobListLifecycle,
): Nto
