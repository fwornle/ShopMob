package com.tanfra.shopmob.smob.data.local.dto

import androidx.room.*
import com.tanfra.shopmob.utils.*
import java.util.*

/**
 * Immutable model class for a SmobList. In order to compile with Room
 *
 * @param name           name of the smobList
 * @param description    optional description
 * @param products       list of product descriptors (id, state) of the smobList
 * @param lifecycle      lifecycle information of the list (state, completion)
 * @param listId         id of the smobList
 */
@Entity(tableName = "smobLists")
@RewriteQueriesToDropUnusedColumns
data class SmobListDTO(
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "products") var products: List<SmobListEntry>,
    @ColumnInfo(name = "lifecycle") var lifecycle: SmobListLifecycle,
    @PrimaryKey @ColumnInfo(name = "id") val listId: String = UUID.randomUUID().toString()
)
