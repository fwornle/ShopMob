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

    // SmobMemberItem converter - List
    @TypeConverter
    fun listOfMemberEntryToJson(value: List<SmobMemberItem?>) = Json.encodeToString(value)

    @TypeConverter
    fun jsonToListOfMemberEntry(value: String) = Json.decodeFromString<Array<SmobMemberItem>>(value).toList()

    // SmobGroupItem converter - List
    @TypeConverter
    fun listOfGroupEntryToJson(value: List<SmobGroupItem?>) = Json.encodeToString(value)

    @TypeConverter
    fun jsonToListOfGroupEntry(value: String) = Json.decodeFromString<Array<SmobGroupItem>>(value).toList()

    // SmobListItem converter - List
    @TypeConverter
    fun listOfListEntryToJson(value: List<SmobListItem?>) = Json.encodeToString(value)

    @TypeConverter
    fun jsonToListofListEntry(value: String) = Json.decodeFromString<Array<SmobListItem>>(value).toList()

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
