package com.tanfra.shopmob.smob.data.net.nto

import com.tanfra.shopmob.smob.data.local.utils.*

// network data type
data class SmobListNTO(
    override val id: String,
    override var itemStatus: ItemStatus,
    override var itemPosition: Long,
    var name: String,
    var description: String?,
    var items: List<SmobListItem>,
    var groups: List<SmobGroupItem>,
    var lifecycle: SmobListLifecycle,
): Nto
