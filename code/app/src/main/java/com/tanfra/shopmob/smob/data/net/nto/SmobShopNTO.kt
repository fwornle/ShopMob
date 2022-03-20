package com.tanfra.shopmob.smob.data.net.nto

import com.tanfra.shopmob.smob.data.local.utils.ShopCategory
import com.tanfra.shopmob.smob.data.local.utils.ShopLocation
import com.tanfra.shopmob.smob.data.local.utils.ShopType
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus

// network data type
data class SmobShopNTO(
    override val id: String,
    override var itemStatus: SmobItemStatus,
    override var itemPosition: Long,
    var name: String,
    var description: String?,
    var imageUrl: String?,
    var location: ShopLocation,
    var type: ShopType,
    var category: ShopCategory,
    var business: List<String>,
): Nto
