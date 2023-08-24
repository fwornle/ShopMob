package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.GroupType
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.SmobGroupItem
import com.tanfra.shopmob.smob.data.types.SmobListItem
import com.tanfra.shopmob.smob.data.types.SmobListLifecycle
import com.tanfra.shopmob.smob.data.types.SmobMemberItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


// domain independent data type (Application Transfer Object)
@Serializable
@SerialName("smobGroupWithListDataATO")
data class SmobGroupWithListDataATO(
    override val id: String,
    override var status: ItemStatus,
    override var position: Long,
    var groupName: String,
    var groupDescription: String?,
    var groupType: GroupType,
    var groupMembers: List<SmobMemberItem>,
    var groupActivity: ActivityStatus,
    val listId: String,
    val listStatus: ItemStatus,
    val listPosition: Long,
    var listName: String,
    var listDescription: String?,
    var listItems: List<SmobListItem>,
    var listGroups: List<SmobGroupItem>,
    var listLifecycle: SmobListLifecycle,
) : Ato {

    // extract group
    fun group() = SmobGroupATO(
        this.id,
        this.status,
        this.position,
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