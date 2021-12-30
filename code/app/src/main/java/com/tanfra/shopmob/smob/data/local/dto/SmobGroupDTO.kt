package com.tanfra.shopmob.smob.data.local.dto

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.utils.GroupType
import java.time.LocalDate
import java.util.*

/**
 * Immutable model class for a SmobGroup. In order to compile with Room
 *
 * @param id             id of the smobGroup
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
    @PrimaryKey @ColumnInfo(name = "id") val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "type") var type: GroupType,
    @ColumnInfo(name = "members") var members: List<String>,
    @ColumnInfo(name = "activity_date") var activityDate: LocalDate,
    @ColumnInfo(name = "activity_reps") var activityReps: Long,
)
