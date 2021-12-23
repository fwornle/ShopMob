package com.tanfra.shopmob.smob.data.local

import android.content.Context
import androidx.room.Room
import com.tanfra.shopmob.smob.data.local.dao.*


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

    // TODO: change to smobLists
    // DAO to table smobItems
    fun createSmobItemDao(db: SmobDatabase): SmobItemDao {
        return db.smobItemDao()
    }

    // DAO to table smobUsers
    fun createSmobUserDao(db: SmobDatabase): SmobUserDao {
        return db.smobUserDao()
    }

    // DAO to table smobGroups
    fun createSmobGroupDao(db: SmobDatabase): SmobGroupDao {
        return db.smobGroupDao()
    }

    // DAO to table smobShops
    fun createSmobShopDao(db: SmobDatabase): SmobShopDao {
        return db.smobShopDao()
    }

    // DAO to table smobProducts
    fun createSmobProductDao(db: SmobDatabase): SmobProductDao {
        return db.smobProductDao()
    }

}