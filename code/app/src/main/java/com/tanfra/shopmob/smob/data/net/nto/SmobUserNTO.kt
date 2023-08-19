package com.tanfra.shopmob.smob.data.net.nto

import com.squareup.moshi.JsonClass
import com.tanfra.shopmob.smob.data.types.ItemStatus


// network data type
@JsonClass(generateAdapter = true)  // use moshi codegen (via KSP annotation processor)
 class SmobUserNTO(
    override val id: String,
    override val status: ItemStatus,
    override val position: Long,
    val username: String,
    val name: String,
    val email: String,
    val imageUrl: String?,
    val groups: List<String>,
): Nto