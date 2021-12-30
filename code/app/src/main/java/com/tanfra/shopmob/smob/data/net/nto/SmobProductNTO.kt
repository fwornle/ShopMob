package com.tanfra.shopmob.smob.data.net.nto

import com.tanfra.shopmob.smob.data.local.utils.ActivityState
import com.tanfra.shopmob.smob.data.local.utils.ProductCategory

// network data type
data class SmobProductNTO(
    val id: String,
    var name: String,
    var description: String?,
    var imageUrl: String?,
    var category: ProductCategory,
    var activity: ActivityState,
)

