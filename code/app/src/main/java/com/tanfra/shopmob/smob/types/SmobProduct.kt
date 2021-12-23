package com.tanfra.shopmob.smob.types

import com.tanfra.shopmob.utils.ActivityState
import com.tanfra.shopmob.utils.ProductCategory
import com.tanfra.shopmob.utils.ProductMainCategory
import com.tanfra.shopmob.utils.ProductSubCategory
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

// domain independent data type
@Serializable
data class SmobProduct(
    val id: String,
    var name: String,
    var description: String?,
    var image: String?,
    var category: @Contextual ProductCategory,
    var activityState: @Contextual ActivityState,
)