package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.local.utils.ShopCategory
import com.tanfra.shopmob.smob.data.local.utils.ShopLocation
import com.tanfra.shopmob.smob.data.local.utils.ShopType
import com.tanfra.shopmob.smob.data.local.utils.ItemStatus
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

// domain independent data type (Application Transfer Object)
@Serializable
data class SmobShopATO(
    override val itemId: String,
    override var itemStatus: @Contextual ItemStatus,
    override var itemPosition: Long,
    var name: String,
    var description: String?,
    var imageUrl: String?,
    var location: @Contextual ShopLocation,
    var type: ShopType,
    var category: ShopCategory,
    var business: List<String>,
) : Ato, java.io.Serializable