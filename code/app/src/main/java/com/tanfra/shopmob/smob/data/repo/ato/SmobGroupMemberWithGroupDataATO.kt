package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.GroupType
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.SmobItemId
import com.tanfra.shopmob.smob.data.types.SmobItemPosition
import com.tanfra.shopmob.smob.data.types.SmobMemberItem
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

// domain independent data type (Application Transfer Object)
@Serializable
data class SmobGroupMemberWithGroupDataATO(
    override val itemId: @Contextual SmobItemId,
    override var itemStatus: @Contextual ItemStatus,
    override var itemPosition: @Contextual SmobItemPosition,
    var memberUsername: String,
    var memberName: String,
    var memberEmail: String,
    var memberImageUrl: String?,
    var memberGroups: List<String>,
    val groupId: String,
    val groupStatus: @Contextual ItemStatus,
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
        this.itemId,
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
        SmobItemId(this.groupId),
        this.groupStatus,
        SmobItemPosition(this.groupPosition),
        this.groupName,
        this.groupDescription,
        this.groupType,
        this.groupMembers,
        this.groupActivity,
    )

}