package com.tanfra.shopmob.smob.data.net.nto

import com.tanfra.shopmob.smob.data.types.ShopCategory
import com.tanfra.shopmob.smob.data.types.ShopLocation
import com.tanfra.shopmob.smob.data.types.ShopType
import com.tanfra.shopmob.smob.data.types.ItemStatus

// network data type
data class SmobShopNTO(
    override val itemId: String,
    override var itemStatus: ItemStatus,
    override var itemPosition: Long,
    var name: String,
    var description: String?,
    var imageUrl: String?,
    var location: ShopLocation,
    var type: ShopType,
    var category: ShopCategory,
    var business: List<String>,
): Nto
