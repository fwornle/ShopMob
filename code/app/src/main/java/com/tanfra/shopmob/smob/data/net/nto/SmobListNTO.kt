package com.tanfra.shopmob.smob.data.net.nto

import com.tanfra.shopmob.smob.data.local.utils.SmobListItem
import com.tanfra.shopmob.smob.data.local.utils.SmobListLifecycle

// network data type
data class SmobListNTO(
    val id: String,
    var name: String,
    var description: String?,
    var items: List<SmobListItem>,
    var members: List<String>,
    var lifecycle: SmobListLifecycle,
)

