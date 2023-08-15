package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.types.ShopCategory
import com.tanfra.shopmob.smob.data.types.ShopLocation
import com.tanfra.shopmob.smob.data.types.ShopType
import com.tanfra.shopmob.smob.data.types.ItemStatus
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

// domain independent data type (Application Transfer Object)
@Serializable
data class SmobShopATO(
    override val id: String,
    override var status: @Contextual ItemStatus,
    override var position: Long,
    var name: String,
    var description: String?,
    var imageUrl: String?,
    var location: @Contextual ShopLocation,
    var type: ShopType,
    var category: ShopCategory,
    var business: List<String>,
) : Ato, java.io.Serializable