package com.tanfra.shopmob.smob.data.local.dto

import androidx.room.*
import java.util.*

/**
 * Immutable model class for a SmobUser. In order to compile with Room
 *
 * @param username     name of the smobUser
 * @param imageUrl     URL to image/avatar of the smobUser
 * @param shops        list of shops the smobUser has been to
 * @param groups       list of groups the smobUser belongs to
 * @param lists        list of 'shopping lists' the smobUser has subscribed to
 * @param userId       id of the smobUser
 */
@Entity(tableName = "smobUsers")
@RewriteQueriesToDropUnusedColumns
data class SmobUserDTO(
    @ColumnInfo(name = "name") var username: String,
    @ColumnInfo(name = "imageUrl") var imageUrl: String?,
    @ColumnInfo(name = "shops") var shops: List<String?>,
    @ColumnInfo(name = "groups") var groups: List<String?>,
    @ColumnInfo(name = "lists") var lists: List<String?>,
    @PrimaryKey @ColumnInfo(name = "id") val userId: String = UUID.randomUUID().toString()
)

