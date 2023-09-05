package com.tanfra.shopmob.smob.data.local

import android.content.Context
import androidx.room.Room
import com.tanfra.shopmob.smob.data.local.dataSource.SmobGroupLocalDataSource
import com.tanfra.shopmob.smob.data.local.dataSource.SmobListLocalDataSource
import com.tanfra.shopmob.smob.data.local.dataSource.SmobProductLocalDataSource
import com.tanfra.shopmob.smob.data.local.dataSource.SmobShopLocalDataSource
import com.tanfra.shopmob.smob.data.local.dataSource.SmobUserLocalDataSource


/**
 * Singleton class that is used to create a smob database
 */
object LocalDB {

    /**
     * static method that creates a SmobDatabase "Room" class, representing local DB file smob.db
     */
    fun createSmobDatabase(context: Context): SmobDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            SmobDatabase::class.java, "smob.db"
        ).build()
    }

    // DAO to table smobUsers
    fun createSmobUserDao(db: SmobDatabase): SmobUserLocalDataSource {
        return db.smobUserDao()
    }

    // DAO to table smobGroups
    fun createSmobGroupDao(db: SmobDatabase): SmobGroupLocalDataSource {
        return db.smobGroupDao()
    }

    // DAO to table smobShops
    fun createSmobShopDao(db: SmobDatabase): SmobShopLocalDataSource {
        return db.smobShopDao()
    }

    // DAO to table smobProducts
    fun createSmobProductDao(db: SmobDatabase): SmobProductLocalDataSource {
        return db.smobProductDao()
    }

    // DAO to table smobProducts
    fun createSmobListDao(db: SmobDatabase): SmobListLocalDataSource {
        return db.smobListDao()
    }

}