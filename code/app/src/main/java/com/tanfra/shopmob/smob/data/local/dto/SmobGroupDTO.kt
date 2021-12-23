package com.tanfra.shopmob.smob.data.local.dto

import androidx.room.*
import com.tanfra.shopmob.utils.GroupType
import com.tanfra.shopmob.utils.ActivityState
import java.util.*

/**
 * Immutable model class for a SmobGroup. In order to compile with Room
 *
 * @param name           name of the smobGroup
 * @param description    optional description
 * @param type           (default)other|family|friends|work
 * @param members        comma separated list of userIds
 * @param activityState  data class ItemActivity: date of last / frequency of event/s in this group
 * @param groupId        id of the smobGroup
 */
@Entity(tableName = "smobGroups")
@RewriteQueriesToDropUnusedColumns
data class SmobGroupDTO(
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "type") var type: GroupType,
    @ColumnInfo(name = "members") var members: List<String?>,
    @ColumnInfo(name = "activity") var activityState: ActivityState,
    @PrimaryKey @ColumnInfo(name = "id") val groupId: String = UUID.randomUUID().toString()
)
