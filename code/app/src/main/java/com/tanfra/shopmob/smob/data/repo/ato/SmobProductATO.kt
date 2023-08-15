package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.InShop
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.ProductCategory
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

// domain independent data type (Application Transfer Object)
@Serializable
data class SmobProductATO(
    override val id: String,
    override var status: @Contextual ItemStatus,
    override var position: Long,
    var name: String,
    var description: String?,
    var imageUrl: String?,
    var category: @Contextual ProductCategory,
    var activity: @Contextual ActivityStatus,
    var inShop: @Contextual InShop,
) : Ato, java.io.Serializable