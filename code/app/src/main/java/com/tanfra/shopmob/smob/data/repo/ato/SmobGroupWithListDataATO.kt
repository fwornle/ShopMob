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
    override val id: String = "invalid ID",
    override var status: ItemStatus = ItemStatus.INVALID,
    override var position: Long = -1,
    val groupName: String = "invalid name",
    val groupDescription: String? = null,
    val groupType: GroupType = GroupType.OTHER,
    val groupMembers: List<SmobMemberItem> = listOf(),
    val groupActivity: ActivityStatus = ActivityStatus("invalid date", -1),
    val listId: String = "invalid ID",
    val listStatus: ItemStatus = ItemStatus.INVALID,
    val listPosition: Long = -1,
    val listName: String = "invalid name",
    val listDescription: String? = null,
    val listItems: List<SmobListItem> = listOf(),
    val listGroups: List<SmobGroupItem> = listOf(),
    val listLifecycle: SmobListLifecycle = SmobListLifecycle(ItemStatus.INVALID, 0.0),
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