package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.GroupType
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.SmobMemberItem
import kotlinx.serialization.*

// domain independent data type (Application Transfer Object)
@Serializable
@SerialName("smobGroupATO")
data class SmobGroupATO(
    override val id: String = "invalid ID",
    override var status: ItemStatus = ItemStatus.INVALID,
    override var position: Long = -1,
    val name: String = "invalid name",
    val description: String? = null,
    val type: GroupType = GroupType.OTHER,
    var members: List<SmobMemberItem> = listOf(),
    val activity: ActivityStatus = ActivityStatus("invalid date", -1),
) : Ato