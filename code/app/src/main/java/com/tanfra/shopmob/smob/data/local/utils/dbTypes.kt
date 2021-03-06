package com.tanfra.shopmob.smob.data.local.utils

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable
import java.text.FieldPosition

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
    NEW,
    OPEN,
    IN_PROGRESS,
    DONE,
    DELETED,
}

@Serializable
data class InShop(
    val category: ShopCategory,
    val name: String,
    val location: ShopLocation,
) : java.io.Serializable

@Serializable
data class ActivityStatus(
    val date: String,
    val reps: Long,
) : java.io.Serializable

@Serializable
data class ProductCategory(
    var main: ProductMainCategory,
    var sub: ProductSubCategory,
) : java.io.Serializable

@Serializable
data class ShopLocation(
    val latitude: Double,
    val longitude: Double,
) : java.io.Serializable

@Serializable
data class SmobListItem(
    val id: String,
    var status: SmobItemStatus,
    val listPosition: Long,
    val mainCategory: ProductMainCategory,
) : java.io.Serializable

@Serializable
data class SmobMemberItem(
    val id: String,
    var status: SmobItemStatus,
    val listPosition: Long,
) : java.io.Serializable

@Serializable
data class SmobListLifecycle(
    val status: SmobItemStatus,
    var completion: Double
) : java.io.Serializable
