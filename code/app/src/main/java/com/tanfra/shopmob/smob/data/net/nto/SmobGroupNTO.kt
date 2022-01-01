package com.tanfra.shopmob.smob.data.net.nto

import com.tanfra.shopmob.smob.data.local.utils.ActivityStatus
import com.tanfra.shopmob.smob.data.local.utils.GroupType

// network data type
data class SmobGroupNTO(
    val id: String,
    var name: String,
    var description: String?,
    var type: GroupType,
    var members: List<String>,
    val activity: ActivityStatus,
)

