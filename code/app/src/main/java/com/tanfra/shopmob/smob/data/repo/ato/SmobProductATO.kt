package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.local.utils.ActivityStatus
import com.tanfra.shopmob.smob.data.local.utils.ProductCategory
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

// domain independent data type (Application Transfer Object)
@Serializable
data class SmobProductATO(
    val id: String,
    var name: String,
    var description: String?,
    var imageUrl: String?,
    var category: @Contextual ProductCategory,
    var activity: @Contextual ActivityStatus,
) : java.io.Serializable