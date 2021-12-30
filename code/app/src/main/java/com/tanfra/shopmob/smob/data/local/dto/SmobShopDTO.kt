package com.tanfra.shopmob.smob.data.local.dto

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.utils.ShopCategory
import com.tanfra.shopmob.smob.data.local.utils.ShopType
import java.util.*

/**
 * Immutable model class for a SmobShop. In order to compile with Room
 *
 * @param id             id of the smobShop
 * @param name           name of the smobShop
 * @param description    (optional) description of the smobShop
 * @param locLat         latitude of the smobShop
 * @param locLong        longitude of the smobShop
 * @param type           (default)individual|chain
 * @param category       (default)other|supermarket|drugstore|hardware|clothing|accessories|supplies|...
 * @param business       list of 'opening hour strings' (7 - on per day, starting with Monday)
 */
@Entity(tableName = "smobShops")
@RewriteQueriesToDropUnusedColumns
data class SmobShopDTO(
    @PrimaryKey @ColumnInfo(name = "id") val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "location_latitude") var locLat: Double,
    @ColumnInfo(name = "location_longitude") var locLong: Double,
    @ColumnInfo(name = "type") var type: ShopType,
    @ColumnInfo(name = "category") var category: ShopCategory,
    @ColumnInfo(name = "business") var business: List<String>
)