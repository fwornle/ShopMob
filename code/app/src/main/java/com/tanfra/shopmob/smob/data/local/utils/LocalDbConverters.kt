package com.tanfra.shopmob.smob.data.local.utils

import androidx.room.TypeConverter
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString


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

@Serializable
data class SmobListLifecycle(
    val status: SmobItemStatus,
    val completion: Double
) : java.io.Serializable


// serialization/de-serialization of data types for storage in Room DB (mySQL)
// ... ref: https://github.com/Kotlin/kotlinx.serialization
class LocalDbConverters {

    // ItemActivity converter
    @TypeConverter
    fun activityToJson(value: ActivityStatus?) = Json.encodeToString(value)

    @TypeConverter
    fun jsonToActivity(value: String) = Json.decodeFromString<ActivityStatus>(value)

    // SmobListEntry converter - List
    @TypeConverter
    fun listEntryToJson(value: List<SmobListItem?>) = Json.encodeToString(value)

    @TypeConverter
    fun jsonToListEntry(value: String) = Json.decodeFromString<Array<SmobListItem>>(value).toList()

    // SmobListLifecycle converter
    @TypeConverter
    fun listLifecycleToJson(value: SmobListLifecycle?) = Json.encodeToString(value)

    @TypeConverter
    fun jsonToListLifecycle(value: String) = Json.decodeFromString<SmobListLifecycle>(value)

    // (general) List<String> converter
    @TypeConverter
    fun fromString(stringListString: String?): List<String> {
        return stringListString?.split(",")?.map { it } ?: listOf()
    }

    @TypeConverter
    fun toString(stringList: List<String?>): String {
        return stringList.joinToString(separator = ",")
    }

}
