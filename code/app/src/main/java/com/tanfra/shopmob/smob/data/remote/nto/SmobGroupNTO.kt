package com.tanfra.shopmob.smob.data.remote.nto

import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.GroupType
import com.tanfra.shopmob.smob.data.types.SmobMemberItem
import com.tanfra.shopmob.smob.data.types.ItemStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


// network data type
@Serializable
@SerialName("smobGroupNTO")
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