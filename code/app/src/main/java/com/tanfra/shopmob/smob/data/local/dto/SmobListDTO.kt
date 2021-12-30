package com.tanfra.shopmob.smob.data.local.dto

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.utils.SmobEntryState
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
 * @param lcState        lifecycle state information of the list (OPEN|IN PROGRESS|DONE)
 * @param lcCompletion   lifecycle completion information of the list (degree of completion, %)
 */
@Entity(tableName = "smobLists")
@RewriteQueriesToDropUnusedColumns
data class SmobListDTO(
    @PrimaryKey @ColumnInfo(name = "id") val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "items") var items: List<SmobListItem>,
    @ColumnInfo(name = "members") var members: List<String>,
    @ColumnInfo(name = "lifecycle_state") var lcState: SmobEntryState,
    @ColumnInfo(name = "lifecycle_completion") var lcCompletion: Double,
)
