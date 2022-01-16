package com.tanfra.shopmob.smob.data.local.dto

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.utils.*

/**
 * Immutable model class for a SmobProduct. In order to compile with Room
 *
 * @param id             id of the smobProduct
 * @param itemStatus     status of an item of the smobProduct (in a list)
 * @param itemPosition   position of an item of the smobProduct (in a list)
 * @param name           name of the smobProduct
 * @param description    optional description
 * @param imageUrl       URL to image/avatar of the smobProduct
 * @param categoryMain   (default)other|foods|hardware|supplies|clothing|...
 * @param categorySub    (default)other|dairy|bread|fruit_vegetable|canned_food|beverages|...
 * @param activityDate   data class (ActivityState) member "date": of last purchase of this product
 * @param activityReps   data class (ActivityState) member "reps": number of repetitions of purchase
 * @param inShopCategory category of shop in which such a product can be found
 * @param inShopName     name of a specific shop (or a chain) in which this product can be found
 * @param inShopLocation location information of a specific shop in which the product can be found
 */
@Entity(tableName = "smobProducts")
@RewriteQueriesToDropUnusedColumns
data class SmobProductDTO(
    @PrimaryKey @ColumnInfo(name = "productId") var id: String,
    @ColumnInfo(name = "productItemStatus") var itemStatus: SmobItemStatus,
    @ColumnInfo(name = "productItemPosition") var itemPosition: Long,
    @ColumnInfo(name = "productName") var name: String,
    @ColumnInfo(name = "productDescription") var description: String?,
    @ColumnInfo(name = "productImageUrl") var imageUrl: String?,
    @ColumnInfo(name = "productCategoryMain") var categoryMain: ProductMainCategory,
    @ColumnInfo(name = "productCategorySub") var categorySub: ProductSubCategory,
    @ColumnInfo(name = "productActivityDate") var activityDate: String,
    @ColumnInfo(name = "productActivityReps") var activityReps: Long,
    @ColumnInfo(name = "productInShopCategory") var inShopCategory: ShopCategory,
    @ColumnInfo(name = "productInShopName") var inShopName: String,
    @ColumnInfo(name = "productInShopLocation") var inShopLocation: ShopLocation,
)
