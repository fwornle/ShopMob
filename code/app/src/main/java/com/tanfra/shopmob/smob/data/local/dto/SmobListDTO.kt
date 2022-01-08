package com.tanfra.shopmob.smob.data.local.dto

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus
import com.tanfra.shopmob.smob.data.local.utils.SmobListItem
import java.util.*

/**
 * Immutable model class for a SmobList. In order to compile with Room
 *
 * @param id             id of the smobList
 * @param name           name of the smobList
 * @param description    optional description
 * @param items          list of descriptors (id, state) of items on the smobList
 * @param members        list of IDs of the smob users sharing this list
 * @param lcStatus       lifecycle status information of the list (OPEN|IN PROGRESS|DONE)
 * @param lcCompletion   lifecycle completion information of the list (degree of completion, %)
 */
@Entity(tableName = "smobLists")
@RewriteQueriesToDropUnusedColumns
data class SmobListDTO(
    @PrimaryKey @ColumnInfo(name = "listId") var id: String,
    @ColumnInfo(name = "listName") var name: String,
    @ColumnInfo(name = "listDescription") var description: String?,
    @ColumnInfo(name = "listItems") var items: List<SmobListItem>,
    @ColumnInfo(name = "listMembers") var members: List<String>,
    @ColumnInfo(name = "listLifecycleStatus") var lcStatus: SmobItemStatus,
    @ColumnInfo(name = "listLifecycleCompletion") var lcCompletion: Double,
)
