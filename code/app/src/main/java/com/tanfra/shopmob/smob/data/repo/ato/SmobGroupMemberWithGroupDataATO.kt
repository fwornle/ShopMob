package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.local.utils.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

// domain independent data type (Application Transfer Object)
@Serializable
data class SmobGroupMemberWithGroupDataATO(
    override val id: String,
    override var itemStatus: @Contextual SmobItemStatus,
    override var itemPosition: Long,
    var memberUsername: String,
    var memberName: String,
    var memberEmail: String,
    var memberImageUrl: String?,
    var memberGroups: List<String>,
    val groupId: String,
    val groupStatus: @Contextual SmobItemStatus,
    val groupPosition: Long,
    var groupName: String,
    var groupDescription: String?,
    var groupType: GroupType,
    var groupMembers: List<@Contextual SmobMemberItem>,
    // serialization strategy decided at run-time (@Contextual)
    var groupActivity: @Contextual ActivityStatus,
) : Ato, java.io.Serializable {

    // extract member
    fun member() = SmobUserATO(
        this.id,
        this.itemStatus,
        this.itemPosition,
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