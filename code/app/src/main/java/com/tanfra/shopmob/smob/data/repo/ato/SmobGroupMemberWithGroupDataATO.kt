package com.tanfra.shopmob.smob.data.repo.ato

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
    override val id: String,
    override var status: ItemStatus,
    override var position: Long,
    var memberUsername: String,
    var memberName: String,
    var memberEmail: String,
    var memberImageUrl: String?,
    var memberGroups: List<String>,
    val groupId: String,
    val groupStatus: ItemStatus,
    val groupPosition: Long,
    var groupName: String,
    var groupDescription: String?,
    var groupType: GroupType,
    var groupMembers: List<SmobMemberItem>,
    var groupActivity: ActivityStatus,
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