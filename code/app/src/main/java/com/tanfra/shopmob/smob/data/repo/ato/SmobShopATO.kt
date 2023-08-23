package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.types.ShopCategory
import com.tanfra.shopmob.smob.data.types.ShopLocation
import com.tanfra.shopmob.smob.data.types.ShopType
import com.tanfra.shopmob.smob.data.types.ItemStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


// domain independent data type (Application Transfer Object)
@Serializable
@SerialName("smobShop")
data class SmobShopATO(
    override val id: String,
    override var status: ItemStatus,
    override var position: Long,
    var name: String,
    var description: String?,
    var imageUrl: String?,
    var location: ShopLocation,
    var type: ShopType,  // clashes w/h JSON class discriminator 'type'
    var category: ShopCategory,
    var business: List<String>,
) : Ato