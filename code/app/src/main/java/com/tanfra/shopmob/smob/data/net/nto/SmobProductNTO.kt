package com.tanfra.shopmob.smob.data.net.nto

import com.tanfra.shopmob.smob.data.local.utils.*

// network data type
data class SmobProductNTO(
    val id: String,
    var itemStatus: SmobItemStatus,
    var itemPosition: Long,
    var name: String,
    var description: String?,
    var imageUrl: String?,
    var category: ProductCategory,
    var activity: ActivityStatus,
    var inShop: InShop,
)

