package com.tanfra.shopmob.smob.data.net.nto

import com.squareup.moshi.JsonClass
import com.tanfra.shopmob.smob.data.types.ShopCategory
import com.tanfra.shopmob.smob.data.types.ShopLocation
import com.tanfra.shopmob.smob.data.types.ShopType
import com.tanfra.shopmob.smob.data.types.ItemStatus


// network data type
@JsonClass(generateAdapter = true)  // use moshi codegen (via KSP annotation processor)
data class SmobShopNTO(
    override val id: String,
    override val status: ItemStatus,
    override val position: Long,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val location: ShopLocation,
    val type: ShopType,
    val category: ShopCategory,
    val business: List<String>,
): Nto
