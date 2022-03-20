package com.tanfra.shopmob.smob.data.local.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RewriteQueriesToDropUnusedColumns
import com.tanfra.shopmob.smob.data.local.utils.*

/**
 * supertype, common to all DTO types - generic part of any DTO class
 * (properties declared abstract --> implementation delegated to inheriting concrete class)
 */
sealed class Dto {
    abstract val id: String
    abstract var itemStatus: SmobItemStatus
    abstract var itemPosition: Long
}