package com.tanfra.shopmob.smob.data.local.dto

import androidx.room.*
import com.tanfra.shopmob.utils.*
import java.util.*

/**
 * Immutable model class for a SmobList. In order to compile with Room
 *
 * @param id             id of the smobList
 * @param name           name of the smobList
 * @param description    optional description
 * @param products       list of product descriptors (id, state) of the smobList
 * @param lifecycle      lifecycle information of the list (state, completion)
 */
@Entity(tableName = "smobLists")
@RewriteQueriesToDropUnusedColumns
data class SmobListDTO(
    @PrimaryKey @ColumnInfo(name = "id") val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "products") var products: List<SmobListEntry>,
    @ColumnInfo(name = "lifecycle") var lifecycle: SmobListLifecycle
)
