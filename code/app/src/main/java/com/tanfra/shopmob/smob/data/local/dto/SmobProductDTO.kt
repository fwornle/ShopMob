package com.tanfra.shopmob.smob.data.local.dto

import androidx.room.*
import com.tanfra.shopmob.utils.ActivityState
import com.tanfra.shopmob.utils.ProductMainCategory
import com.tanfra.shopmob.utils.ProductSubCategory
import java.util.*

/**
 * Immutable model class for a SmobProduct. In order to compile with Room
 *
 * @param id             id of the smobProduct
 * @param name           name of the smobProduct
 * @param description    optional description
 * @param image          URL to image/avatar of the smobProduct
 * @param categoryMain   (default)other|foods|hardware|supplies|clothing|...
 * @param categorySub    (default)other|dairy|bread|fruit_vegetable|canned_food|beverages|...
 * @param activityState  data class ItemActivity: date of last / frequency of purchase/s in this product
 */
@Entity(tableName = "smobProducts")
@RewriteQueriesToDropUnusedColumns
data class SmobProductDTO(
    @PrimaryKey @ColumnInfo(name = "id") val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "image") var image: String?,
    @ColumnInfo(name = "category_main") var categoryMain: ProductMainCategory,
    @ColumnInfo(name = "category_sub") var categorySub: ProductSubCategory,
    @ColumnInfo(name = "activity") var activityState: ActivityState
)
