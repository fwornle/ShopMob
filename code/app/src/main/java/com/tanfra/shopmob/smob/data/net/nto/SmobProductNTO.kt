package com.tanfra.shopmob.smob.data.net.nto

import com.squareup.moshi.JsonClass
import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.InShop
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.ProductCategory


// network data type
@JsonClass(generateAdapter = true)  // use moshi codegen (via KSP annotation processor)
data class SmobProductNTO(
    override val id: String,
    override val status: ItemStatus,
    override val position: Long,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val category: ProductCategory,
    val activity: ActivityStatus,
    val inShop: InShop,
): Nto
