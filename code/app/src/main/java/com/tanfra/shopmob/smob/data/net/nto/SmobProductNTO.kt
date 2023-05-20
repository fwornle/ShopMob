package com.tanfra.shopmob.smob.data.net.nto

import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.InShop
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.ProductCategory

// network data type
data class SmobProductNTO(
    override val itemId: String,
    override var itemStatus: ItemStatus,
    override var itemPosition: Long,
    var name: String,
    var description: String?,
    var imageUrl: String?,
    var category: ProductCategory,
    var activity: ActivityStatus,
    var inShop: InShop,
): Nto
