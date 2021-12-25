package com.tanfra.shopmob.smob.types

import com.tanfra.shopmob.utils.SmobListEntry
import com.tanfra.shopmob.utils.SmobListLifecycle
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

// domain independent data type
@Serializable
data class SmobList(
    val id: String,
    var name: String,
    var description: String?,
    var products: List<@Contextual SmobListEntry>,
    var lifecycle: @Contextual SmobListLifecycle,
) : java.io.Serializable