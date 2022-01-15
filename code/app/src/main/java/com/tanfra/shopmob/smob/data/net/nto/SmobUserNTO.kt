package com.tanfra.shopmob.smob.data.net.nto

import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus

// network data type
data class SmobUserNTO(
    val id: String,
    var itemStatus: SmobItemStatus,
    var itemPosition: Long,
    var username: String,
    var name: String,
    var email: String,
    var imageUrl: String?,
)

