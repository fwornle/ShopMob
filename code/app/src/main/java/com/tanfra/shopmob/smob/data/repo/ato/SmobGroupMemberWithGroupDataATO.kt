package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.app.Constants.INVALID_ITEM_ID
import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.GroupType
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.SmobMemberItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// domain independent data type (Application Transfer Object)
@Serializable
@SerialName("smobGroupMemberWithGroupDataATO")
data class SmobGroupMemberWithGroupDataATO(
    override val id: String = INVALID_ITEM_ID,
    override var status: ItemStatus = ItemStatus.INVALID,
    override var position: Long = -1,
    val memberUsername: String = "invalid user name",
    val memberName: String = "invalid name",
    val memberEmail: String = "invalid email",
    val memberImageUrl: String? = null,
    val memberGroups: List<String> = listOf(),
    val groupId: String = INVALID_ITEM_ID,
    val groupStatus: ItemStatus = ItemStatus.INVALID,
    val groupPosition: Long = -1,
    val groupName: String = "invalid name",
    val groupDescription: String? = null,
    val groupType: GroupType = GroupType.OTHER,
    val groupMembers: List<SmobMemberItem> = listOf(),
    val groupActivity: ActivityStatus = ActivityStatus("invalid date", -1),
) : Ato {

    // extract member
    fun member() = SmobUserATO(
        this.id,
        this.status,
        this.position,
        this.memberUsername,
        this.memberName,
        this.memberEmail,
        this.memberImageUrl,
        this.memberGroups,
    )

    // extract group
    fun group() = SmobGroupATO(
        this.groupId,
        this.groupStatus,
        this.groupPosition,
        this.groupName,
        this.groupDescription,
        this.groupType,
        this.groupMembers,
        this.groupActivity,
    )

}