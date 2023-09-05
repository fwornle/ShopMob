package com.tanfra.shopmob.smob.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tanfra.shopmob.smob.data.local.dataSource.SmobGroupLocalDataSource
import com.tanfra.shopmob.smob.data.local.dataSource.SmobListLocalDataSource
import com.tanfra.shopmob.smob.data.local.dataSource.SmobProductLocalDataSource
import com.tanfra.shopmob.smob.data.local.dataSource.SmobShopLocalDataSource
import com.tanfra.shopmob.smob.data.local.dataSource.SmobUserLocalDataSource
import com.tanfra.shopmob.smob.data.local.dto.*
import com.tanfra.shopmob.smob.data.local.utils.LocalDbConverters

// Room database for the app - local storage
// ... using separate DAO per DB table to keep things tidy
// ... entities: the various tables in the DB (represented by their DTO data classes)
// ... adding "global" type converter to allow for the usage of List<String> types (CSV)
@TypeConverters(LocalDbConverters::class)
@Database(
    entities = [
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
        abstract fun smobUserDao(): SmobUserLocalDataSource
        abstract fun smobGroupDao(): SmobGroupLocalDataSource
        abstract fun smobShopDao(): SmobShopLocalDataSource
        abstract fun smobProductDao(): SmobProductLocalDataSource
        abstract fun smobListDao(): SmobListLocalDataSource
    }
