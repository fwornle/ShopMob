package com.tanfra.shopmob.smob.data.net.nto

import com.tanfra.shopmob.smob.data.local.utils.ItemStatus

// network data type
data class SmobUserNTO(
    override val id: String,
    override var itemStatus: ItemStatus,
    override var itemPosition: Long,
    var username: String,
    var name: String,
    var email: String,
    var imageUrl: String?,
    var groups: List<String>,
): Nto