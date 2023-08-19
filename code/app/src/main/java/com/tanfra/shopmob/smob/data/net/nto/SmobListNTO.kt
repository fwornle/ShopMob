package com.tanfra.shopmob.smob.data.net.nto

import com.squareup.moshi.JsonClass
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.SmobGroupItem
import com.tanfra.shopmob.smob.data.types.SmobListItem
import com.tanfra.shopmob.smob.data.types.SmobListLifecycle


// network data type
@JsonClass(generateAdapter = true)  // use moshi codegen (via KSP annotation processor)
data class SmobListNTO(
    override val id: String,
    override val status: ItemStatus,
    override val position: Long,
    val name: String,
    val description: String?,
    val items: List<SmobListItem>,
    val groups: List<SmobGroupItem>,
    val lifecycle: SmobListLifecycle,
): Nto
