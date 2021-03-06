package com.tanfra.shopmob.smob.data.local.dto

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.utils.ShopCategory
import com.tanfra.shopmob.smob.data.local.utils.ShopType
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus

/**
 * Immutable model class for a SmobShop. In order to compile with Room
 *
 * @param id             id of the smobShop
 * @param itemStatus     status of an item of the smobShop (in a list)
 * @param itemPosition   position of an item of the smobShop (in a list)
 * @param name           name of the smobShop
 * @param description    (optional) description of the smobShop
 * @param imageUrl       (optional) URL to image of the smobShop
 * @param locLat         latitude of the smobShop
 * @param locLong        longitude of the smobShop
 * @param type           (default)individual|chain
 * @param category       (default)other|supermarket|drugstore|hardware|clothing|accessories|supplies|...
 * @param business       list of 'opening hour strings' (7 - on per day, starting with Monday)
 */
@Entity(tableName = "smobShops")
@RewriteQueriesToDropUnusedColumns
data class SmobShopDTO(
    @PrimaryKey @ColumnInfo(name = "shopId") override val id: String = "invalid smob shop id",
    @ColumnInfo(name = "shopItemStatus") override var itemStatus: SmobItemStatus = SmobItemStatus.NEW,
    @ColumnInfo(name = "shopItemPosition") override var itemPosition: Long = -1L,
    @ColumnInfo(name = "shopName") var name: String = "",
    @ColumnInfo(name = "shopDescription") var description: String? = "",
    @ColumnInfo(name = "shopImageUrl") var imageUrl: String? = "",
    @ColumnInfo(name = "shopLocationLatitude") var locLat: Double = 0.0,
    @ColumnInfo(name = "shopLocationLongitude") var locLong: Double = 0.0,
    @ColumnInfo(name = "shopType") var type: ShopType = ShopType.INDIVIDUAL,
    @ColumnInfo(name = "shopCategory") var category: ShopCategory = ShopCategory.OTHER,
    @ColumnInfo(name = "shopBusiness") var business: List<String> = listOf()
) : Dto()
