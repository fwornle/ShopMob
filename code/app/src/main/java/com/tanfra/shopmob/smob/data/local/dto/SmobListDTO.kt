package com.tanfra.shopmob.smob.data.local.dto

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.utils.SmobGroupItem
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus
import com.tanfra.shopmob.smob.data.local.utils.SmobListItem

/**
 * Immutable model class for a SmobList. In order to compile with Room
 *
 * @param id             id of the smobList
 * @param itemStatus     status of an item of the smobList (in a list)
 * @param itemPosition   position of an item of the smobList (in a list)
 * @param name           name of the smobList
 * @param description    optional description
 * @param items          list of descriptors (id, state) of items on the smobList
 * @param groups         list of IDs of the smob groups sharing this list
 * @param lcStatus       lifecycle status information of the list (OPEN|IN PROGRESS|DONE)
 * @param lcCompletion   lifecycle completion information of the list (degree of completion, %)
 */
@Entity(tableName = "smobLists")
@RewriteQueriesToDropUnusedColumns
data class SmobListDTO(
    @PrimaryKey @ColumnInfo(name = "listId") override val id: String = "invalid smob list id",
    @ColumnInfo(name = "listItemStatus") override var itemStatus: SmobItemStatus = SmobItemStatus.NEW,
    @ColumnInfo(name = "listItemPosition") override var itemPosition: Long = -1L,
    @ColumnInfo(name = "listName") var name: String = "",
    @ColumnInfo(name = "listDescription") var description: String? = "",
    @ColumnInfo(name = "listItems") var items: List<SmobListItem> = listOf(),
    @ColumnInfo(name = "listGroups") var groups: List<SmobGroupItem> = listOf(),
    @ColumnInfo(name = "listLifecycleStatus") var lcStatus: SmobItemStatus = SmobItemStatus.OPEN,
    @ColumnInfo(name = "listLifecycleCompletion") var lcCompletion: Double = -1.0,
) : Dto()
