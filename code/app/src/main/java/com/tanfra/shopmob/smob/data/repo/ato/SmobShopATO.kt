package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.app.Constants.INVALID_SMOB_ITEM_ID
import com.tanfra.shopmob.smob.data.types.ShopCategory
import com.tanfra.shopmob.smob.data.types.ShopLocation
import com.tanfra.shopmob.smob.data.types.ShopType
import com.tanfra.shopmob.smob.data.types.ItemStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// domain independent data type (Application Transfer Object)
@Serializable
@SerialName("smobShopATO")
data class SmobShopATO(
    override val id: String = INVALID_SMOB_ITEM_ID,
    override var status: ItemStatus = ItemStatus.INVALID,
    override var position: Long = -1L,
    var name: String = "invalidName",
    var description: String? = null,
    val imageUrl: String? = null,
    var location: ShopLocation = ShopLocation(0.0, 0.0),
    val type: ShopType = ShopType.INDIVIDUAL,
    var category: ShopCategory = ShopCategory.OTHER,
    val business: List<String> = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
) : Ato