package com.tanfra.shopmob.smob.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tanfra.shopmob.smob.data.dto.SmobItemDTO

/**
 * The Room Database that contains the smob items table.
 */
@Database(entities = [SmobItemDTO::class], version = 1, exportSchema = false)
abstract class SmobItemDatabase : RoomDatabase() {

    abstract fun smobItemDao(): SmobItemDao
}