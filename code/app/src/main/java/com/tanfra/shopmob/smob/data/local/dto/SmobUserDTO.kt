package com.tanfra.shopmob.smob.data.local.dto

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.utils.ItemStatus

/**
 * Immutable model class for a SmobUser. In order to compile with Room
 *
 * @param itemId         itemId of the smobUser
 * @param itemStatus     status of an item of the smobUser (in a list)
 * @param itemPosition   position of an item of the smobUser (in a list)
 * @param username       username of the smobUser
 * @param name           name of the smobUser
 * @param email          email of the smobUser
 * @param imageUrl       (optional) URL to image of the smobUser
 * @param groups         list of group IDs the user has subscribed to
 */
@Entity(tableName = "smobUsers")
@RewriteQueriesToDropUnusedColumns
data class SmobUserDTO(
    @PrimaryKey @ColumnInfo(name = "userId") override var itemId: String = "invalid smob user id",
    @ColumnInfo(name = "userItemStatus") override var itemStatus: ItemStatus = ItemStatus.NEW,
    @ColumnInfo(name = "userItemPosition") override var itemPosition: Long = -1L,
    @ColumnInfo(name = "userUsername") var username: String = "",
    @ColumnInfo(name = "userName") var name: String = "",
    @ColumnInfo(name = "userEmail") var email: String = "",
    @ColumnInfo(name = "userImageUrl") var imageUrl: String? = "",
    @ColumnInfo(name = "userGroups") var groups: List<String> = listOf(),
) : Dto()
