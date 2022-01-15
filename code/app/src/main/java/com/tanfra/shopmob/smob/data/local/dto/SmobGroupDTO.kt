package com.tanfra.shopmob.smob.data.local.dto

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.utils.GroupType
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus
import kotlinx.serialization.Contextual
import java.time.LocalDate
import java.util.*

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
    @PrimaryKey @ColumnInfo(name = "groupId") var id: String,
    @ColumnInfo(name = "groupItemStatus") var itemStatus: SmobItemStatus,
    @ColumnInfo(name = "groupItemPosition") var itemPosition: Long,
    @ColumnInfo(name = "groupName") var name: String,
    @ColumnInfo(name = "groupDescription") var description: String?,
    @ColumnInfo(name = "groupType") var type: GroupType,
    @ColumnInfo(name = "groupMembers") var members: List<String>,
    @ColumnInfo(name = "groupActivityDate") var activityDate: String,
    @ColumnInfo(name = "groupActivityReps") var activityReps: Long,
)
