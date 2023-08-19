package com.tanfra.shopmob.smob.data.net.nto

import com.squareup.moshi.JsonClass
import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.GroupType
import com.tanfra.shopmob.smob.data.types.SmobMemberItem
import com.tanfra.shopmob.smob.data.types.ItemStatus


// network data type
@JsonClass(generateAdapter = true)  // use moshi codegen (via KSP annotation processor)
data class SmobGroupNTO(
    override val id: String,
    override val status: ItemStatus,
    override val position: Long,
    val name: String,
    val description: String?,
    val type: GroupType,
    val members: List<SmobMemberItem>,
    val activity: ActivityStatus,
): Nto