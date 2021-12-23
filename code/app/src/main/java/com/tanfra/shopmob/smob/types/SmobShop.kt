package com.tanfra.shopmob.smob.types

import com.tanfra.shopmob.utils.ShopCategory
import com.tanfra.shopmob.utils.ShopType
import kotlinx.serialization.Serializable

// domain independent data type
@Serializable
data class SmobShop(
    val id: String,
    var name: String,
    var description: String?,
    var type: ShopType,
    var category: ShopCategory,
    var business: List<String?>,
)