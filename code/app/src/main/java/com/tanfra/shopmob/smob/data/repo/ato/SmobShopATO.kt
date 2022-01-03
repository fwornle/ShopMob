package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.local.utils.ShopCategory
import com.tanfra.shopmob.smob.data.local.utils.ShopLocation
import com.tanfra.shopmob.smob.data.local.utils.ShopType
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

// domain independent data type (Application Transfer Object)
@Serializable
data class SmobShopATO(
    override val id: String,
    var name: String,
    var description: String?,
    var location: @Contextual ShopLocation,
    var type: ShopType,
    var category: ShopCategory,
    var business: List<String>,
) : Ato(), java.io.Serializable