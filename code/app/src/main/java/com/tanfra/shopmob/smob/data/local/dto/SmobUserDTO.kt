package com.tanfra.shopmob.smob.data.local.dto

import androidx.room.*
import java.util.*

// DTO for class SmobUser
@Entity(tableName = "smobUsers")
@RewriteQueriesToDropUnusedColumns
data class SmobUserDTO(
    @PrimaryKey @ColumnInfo(name = "userId") val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "userUserame") var username: String,
    @ColumnInfo(name = "userName") var name: String,
    @ColumnInfo(name = "userEmail") var email: String,
    @ColumnInfo(name = "userImageUrl") var imageUrl: String?
)

