package com.tanfra.shopmob.smob.data.net.nto

import com.tanfra.shopmob.smob.data.local.utils.ActivityStatus
import com.tanfra.shopmob.smob.data.local.utils.ProductCategory
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus

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
)

