package com.tanfra.shopmob.smob.data.net.nto

import com.tanfra.shopmob.smob.data.local.utils.ShopCategory
import com.tanfra.shopmob.smob.data.local.utils.ShopLocation
import com.tanfra.shopmob.smob.data.local.utils.ShopType

// network data type
data class SmobShopNTO(
    val id: String,
    var name: String,
    var description: String?,
    var location: ShopLocation,
    var type: ShopType,
    var category: ShopCategory,
    var business: List<String>,
)

