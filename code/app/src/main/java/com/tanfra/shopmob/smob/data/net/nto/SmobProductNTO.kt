package com.tanfra.shopmob.smob.data.net.nto

import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.InShop
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.ProductCategory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


// network data type
@Serializable
@SerialName("smobProductNTO")
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
