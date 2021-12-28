package com.tanfra.shopmob.smob.data.local.dto

import androidx.room.*
import java.util.*

// DTO for class SmobUser
@Entity(tableName = "smobUsers")
@RewriteQueriesToDropUnusedColumns
data class SmobUserDTO(
    @PrimaryKey @ColumnInfo(name = "id") val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "username") var username: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "email") var email: String,
    @ColumnInfo(name = "imageUrl") var imageUrl: String?
)

