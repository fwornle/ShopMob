package com.tanfra.shopmob.smob.data.local.dto

import androidx.room.*
import com.tanfra.shopmob.utils.GroupType
import com.tanfra.shopmob.utils.ActivityState
import java.util.*

/**
 * Immutable model class for a SmobGroup. In order to compile with Room
 *
 * @param id             id of the smobGroup
 * @param name           name of the smobGroup
 * @param description    optional description
 * @param type           (default)other|family|friends|work
 * @param members        comma separated list of userIds
 * @param activityState  data class ItemActivity: date of last / frequency of event/s in this group
 */
@Entity(tableName = "smobGroups")
@RewriteQueriesToDropUnusedColumns
data class SmobGroupDTO(
    @PrimaryKey @ColumnInfo(name = "id") val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "type") var type: GroupType,
    @ColumnInfo(name = "members") var members: List<String?>,
    @ColumnInfo(name = "activity") var activityState: ActivityState
)
