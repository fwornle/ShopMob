package com.tanfra.shopmob.smob.data.local.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RewriteQueriesToDropUnusedColumns
import java.util.*

/**
 * Immutable model class for a SmobItem. In order to compile with Room
 *
 * @param id            id of the smobItem
 * @param title         title of the smobItem
 * @param description   description of the smobItem
 * @param location      location name of the smobItem
 * @param latitude      latitude of the smobItem location
 * @param longitude     longitude of the smobItem location
 */

@Entity(tableName = "smobItems")
@RewriteQueriesToDropUnusedColumns
data class SmobItemDTO(
    @PrimaryKey @ColumnInfo(name = "id") val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "location") var location: String?,
    @ColumnInfo(name = "latitude") var latitude: Double?,
    @ColumnInfo(name = "longitude") var longitude: Double?
)
