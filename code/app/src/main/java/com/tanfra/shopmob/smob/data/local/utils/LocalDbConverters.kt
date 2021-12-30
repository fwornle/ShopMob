package com.tanfra.shopmob.utils

import androidx.room.TypeConverter
import java.time.LocalDate
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
    FRUIT_VEGETABLE,
    CANNED_FOOD,
    BEVERAGES,
}

enum class GroupType {
    OTHER,
    FAMILY,
    FRIENDS,
    WORK,
}

enum class SmobEntryState {
    OPEN,
    IN_PROGRESS,
    DONE,
}

data class ActivityState(
    val date: LocalDate,
    val frequency: Double,
)

data class ProductCategory(
    val main: ProductMainCategory,
    val sub: ProductSubCategory,
)

data class SmobListEntry(
    val id: String,
    val status: SmobEntryState,
)

data class SmobListLifecycle(
    val status: SmobEntryState,
    val completion: Double
)


// serialization/de-serialization of data types for storage in Room DB (mySQL)
// ... ref: https://github.com/Kotlin/kotlinx.serialization
class LocalDbConverters {

    // ItemActivity converter
    @TypeConverter
    fun activityToJson(value: ActivityState?) = Json.encodeToString(value)

    @TypeConverter
    fun jsonToActivity(value: String) = Json.decodeFromString<ActivityState>(value)

    // SmobListEntry converter
    @TypeConverter
    fun listEntryToJson(value: List<SmobListEntry?>) = Json.encodeToString(value)

    @TypeConverter
    fun jsonToListEntry(value: String) = Json.decodeFromString<Array<SmobListEntry>>(value).toList()

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