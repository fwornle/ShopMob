package com.tanfra.shopmob.smob.data.local.utils

import androidx.room.TypeConverter
import kotlinx.serialization.json.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

// serialization/de-serialization of data types for storage in Room DB (mySQL)
// ... ref: https://github.com/Kotlin/kotlinx.serialization
class LocalDbConverters {

    // ShopLocation converter
    @TypeConverter
    fun locationToJson(value: ShopLocation?) = Json.encodeToString(value)

    @TypeConverter
    fun jsonToLocation(value: String) = Json.decodeFromString<ShopLocation>(value)

    // ActivityStatus converter
    @TypeConverter
    fun activityToJson(value: ActivityStatus?) = Json.encodeToString(value)

    @TypeConverter
    fun jsonToActivity(value: String) = Json.decodeFromString<ActivityStatus>(value)

    // SmobListItem converter - List
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
