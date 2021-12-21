package com.tanfra.shopmob.smob.data.local

import android.content.Context
import androidx.room.Room


/**
 * Singleton class that is used to create a smob item database
 */
object LocalDB {

    /**
     * static method that creates a smob item class and returns the DAO of the smob item
     */
    fun createSmobItemDao(context: Context): SmobItemDao {
        return Room.databaseBuilder(
            context.applicationContext,
            SmobItemDatabase::class.java, "smobItems.db"
        ).build().smobItemDao()
    }

}