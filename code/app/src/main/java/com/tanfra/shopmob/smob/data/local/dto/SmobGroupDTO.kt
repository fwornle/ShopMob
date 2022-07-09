package com.tanfra.shopmob.smob.data.local.dto

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.utils.GroupType
import com.tanfra.shopmob.smob.data.local.utils.SmobMemberItem
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus

/**
 * Immutable model class for a SmobGroup. In order to compile with Room
 *
 * @param id             id of the smobGroup
 * @param itemStatus     status of an item of the smobGroup (in a list)
 * @param itemPosition   position of an item of the smobGroup (in a list)
 * @param name           name of the smobGroup
 * @param description    optional description
 * @param type           (default)other|family|friends|work
 * @param members        list of IDs of the smob users sharing this list
 * @param activityDate   data class (ActivityState) member "date": last event within this group
 * @param activityReps   data class (ActivityState) member "reps": number of events
 */
@Entity(tableName = "smobGroups")
@RewriteQueriesToDropUnusedColumns
data class SmobGroupDTO(
    @PrimaryKey @ColumnInfo(name = "groupId") override val id: String = "invalid smob group entry",
    @ColumnInfo(name = "groupItemStatus") override var itemStatus: SmobItemStatus = SmobItemStatus.NEW,
    @ColumnInfo(name = "groupItemPosition") override var itemPosition: Long = -1L,
    @ColumnInfo(name = "groupName") var name: String = "",
    @ColumnInfo(name = "groupDescription") var description: String? = "",
    @ColumnInfo(name = "groupType") var type: GroupType = GroupType.OTHER,
    @ColumnInfo(name = "groupMembers") var members: List<SmobMemberItem> = listOf(),
    @ColumnInfo(name = "groupActivityDate") var activityDate: String = "",
    @ColumnInfo(name = "groupActivityReps") var activityReps: Long = 0,
) : Dto()
