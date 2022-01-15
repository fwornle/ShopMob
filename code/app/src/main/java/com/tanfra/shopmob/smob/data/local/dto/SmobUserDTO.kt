package com.tanfra.shopmob.smob.data.local.dto

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus
import java.util.*

// DTO for class SmobUser
@Entity(tableName = "smobUsers")
@RewriteQueriesToDropUnusedColumns
data class SmobUserDTO(
    @PrimaryKey @ColumnInfo(name = "userId") var id: String,
    @ColumnInfo(name = "userItemStatus") var itemStatus: SmobItemStatus,
    @ColumnInfo(name = "userItemPosition") var itemPosition: Long,
    @ColumnInfo(name = "userUserame") var username: String,
    @ColumnInfo(name = "userName") var name: String,
    @ColumnInfo(name = "userEmail") var email: String,
    @ColumnInfo(name = "userImageUrl") var imageUrl: String?
)

