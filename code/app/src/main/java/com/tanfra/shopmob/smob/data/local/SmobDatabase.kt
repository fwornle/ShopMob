package com.tanfra.shopmob.smob.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tanfra.shopmob.smob.data.local.dao.*
import com.tanfra.shopmob.smob.data.local.dto.*
import com.tanfra.shopmob.smob.data.local.utils.LocalDbConverters

// Room database for the app - local storage
// ... using separate DAO per DB table to keep things tidy
// ... entities: the various tables in the DB (represented by their DTO data classes)
// ... adding "global" type converter to allow for the usage of List<String> types (CSV)
@TypeConverters(LocalDbConverters::class)
@Database(
    entities = [
        SmobItemDTO::class,  // Todo: delete, when no longer needed
        SmobUserDTO::class,
        SmobGroupDTO::class,
        SmobShopDTO::class,
        SmobProductDTO::class,
        SmobListDTO::class,
    ],
    version = 1,
    exportSchema = false
)
    abstract class SmobDatabase : RoomDatabase() {
        abstract fun smobItemDao(): SmobItemDao
        abstract fun smobUserDao(): SmobUserDao
        abstract fun smobGroupDao(): SmobGroupDao
        abstract fun smobShopDao(): SmobShopDao
        abstract fun smobProductDao(): SmobProductDao
        abstract fun smobListDao(): SmobListDao
    }
