package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.types.ShopCategory
import com.tanfra.shopmob.smob.data.types.ShopLocation
import com.tanfra.shopmob.smob.data.types.ShopType
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.SmobItemId
import com.tanfra.shopmob.smob.data.types.SmobItemPosition
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

// domain independent data type (Application Transfer Object)
@Serializable
data class SmobShopATO(
    override val itemId: @Contextual SmobItemId,
    override var itemStatus: @Contextual ItemStatus,
    override var itemPosition: @Contextual SmobItemPosition,
    var name: String,
    var description: String?,
    var imageUrl: String?,
    var location: @Contextual ShopLocation,
    var type: ShopType,
    var category: ShopCategory,
    var business: List<String>,
) : Ato, java.io.Serializable