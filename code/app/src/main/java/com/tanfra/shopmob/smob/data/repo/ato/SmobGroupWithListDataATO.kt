package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.GroupType
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.SmobGroupItem
import com.tanfra.shopmob.smob.data.types.SmobListItem
import com.tanfra.shopmob.smob.data.types.SmobListLifecycle
import com.tanfra.shopmob.smob.data.types.SmobMemberItem
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

// domain independent data type (Application Transfer Object)
@Serializable
data class SmobGroupWithListDataATO(
    override val itemId: String,
    override var itemStatus: @Contextual ItemStatus,
    override var itemPosition: Long,
    var groupName: String,
    var groupDescription: String?,
    var groupType: GroupType,
    var groupMembers: List<SmobMemberItem>,
    // serialization strategy decided at run-time (@Contextual)
    var groupActivity: @Contextual ActivityStatus,
    val listId: String,
    val listStatus: @Contextual ItemStatus,
    val listPosition: Long,
    var listName: String,
    var listDescription: String?,
    var listItems: List<@Contextual SmobListItem>,
    var listGroups: List<@Contextual SmobGroupItem>,
    // serialization strategy decided at run-time (@Contextual)
    var listLifecycle: @Contextual SmobListLifecycle,
) : Ato, java.io.Serializable {

    // extract group
    fun group() = SmobGroupATO(
        this.itemId,
        this.itemStatus,
        this.itemPosition,
        this.groupName,
        this.groupDescription,
        this.groupType,
        this.groupMembers,
        this.groupActivity,
    )

    // extract list
    fun list() = SmobListATO(
        this.listId,
        this.listStatus,
        this.listPosition,
        this.listName,
        this.listDescription,
        this.listItems,
        this.listGroups,
        this.listLifecycle,
    )

}