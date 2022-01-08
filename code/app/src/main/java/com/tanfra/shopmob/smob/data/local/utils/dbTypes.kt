package com.tanfra.shopmob.smob.data.local.utils

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

// define data types to be used with the Room DB (possibly w/h conversion)
enum class ShopCategory {
    OTHER,
    SUPERMARKET,
    DRUGSTORE,
    HARDWARE,
    CLOTHING,
    ACCESSORIES,
    SUPPLIES,
    FURNITURE,
    BAKERY,
}

enum class ShopType {
    INDIVIDUAL,
    CHAIN,
}

enum class ProductMainCategory {
    OTHER,
    FOODS,
    HARDWARE,
    SUPPLIES,
    CLOTHING,
}

enum class ProductSubCategory {
    OTHER,
    DAIRY,
    BREAD,
    BREKKY,
    FRUIT_VEGETABLE,
    CANNED_FOOD,
    BEVERAGES,
    DIY,
    TOOLS,
    OFFICE,
    POSTAL,
    BUSINESS,
    LEISURE,
    SHOES,
}

enum class GroupType {
    OTHER,
    FAMILY,
    FRIENDS,
    WORK,
}

enum class SmobItemStatus {
    OPEN,
    IN_PROGRESS,
    DONE,
    DELETED,
}

@Serializable
data class ActivityStatus(
    val date: String,
    val reps: Long,
) : java.io.Serializable

@Serializable
data class ProductCategory(
    val main: ProductMainCategory,
    val sub: ProductSubCategory,
) : java.io.Serializable

@Serializable
data class ShopLocation(
    val latitude: Double,
    val longitude: Double,
) : java.io.Serializable

@Serializable
data class SmobListItem(
    val id: String,
    val status: SmobItemStatus,
) : java.io.Serializable

//@Serializable
//data class SmobListItemInflated(
//    val itemId: String,
//    val itemName: String,
//    val itemDescription: String?,
//    val itemImageUrl: String?,
//    var itemCategoryMain: ProductMainCategory,
//    var itemCategorySub: ProductSubCategory,
//    var itemActivityDate: String,
//    var itemActivityReps: Long,
//    val itemStatus: SmobItemStatus,
//) : java.io.Serializable

@Serializable
data class SmobListLifecycle(
    val status: SmobItemStatus,
    val completion: Double
) : java.io.Serializable
