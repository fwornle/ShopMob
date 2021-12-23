package com.tanfra.shopmob.smob.data.local.dto

import androidx.room.*
import com.tanfra.shopmob.utils.ShopCategory
import com.tanfra.shopmob.utils.ShopType
import java.util.*

/**
 * Immutable model class for a SmobShop. In order to compile with Room
 *
 * @param shopname       name of the smobShop
 * @param description    (optional) description of the smobShop
 * @param type           (default)individual|chain
 * @param category       (default)other|supermarket|drugstore|hardware|clothing|accessories|supplies|...
 * @param businessHours  list of 'opening hour strings' (7 - on per day, starting with Monday)
 * @param shopId         id of the smobShop
 */
@Entity(tableName = "smobShops")
@RewriteQueriesToDropUnusedColumns
data class SmobShopDTO(
    @ColumnInfo(name = "name") var shopname: String,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "type") var type: ShopType,
    @ColumnInfo(name = "category") var category: ShopCategory,
    @ColumnInfo(name = "business") var businessHours: List<String?>,
    @PrimaryKey @ColumnInfo(name = "id") val shopId: String = UUID.randomUUID().toString()
)