package com.tanfra.shopmob.smob.data.net.nto

import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.GroupType
import com.tanfra.shopmob.smob.data.types.SmobMemberItem
import com.tanfra.shopmob.smob.data.types.ItemStatus

// network data type
data class SmobGroupNTO(
    override val id: String,
    override var status: ItemStatus,
    override var position: Long,
    var name: String,
    var description: String?,
    var type: GroupType,
    var members: List<SmobMemberItem>,
    val activity: ActivityStatus,
): Nto