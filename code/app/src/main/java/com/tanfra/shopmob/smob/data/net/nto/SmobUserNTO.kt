package com.tanfra.shopmob.smob.data.net.nto

import com.tanfra.shopmob.smob.data.types.ItemStatus

// network data type
data class SmobUserNTO(
    override val id: String,
    override var status: ItemStatus,
    override var position: Long,
    var username: String,
    var name: String,
    var email: String,
    var imageUrl: String?,
    var groups: List<String>,
): Nto