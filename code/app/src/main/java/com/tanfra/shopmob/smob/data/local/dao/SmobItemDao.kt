package com.tanfra.shopmob.smob.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tanfra.shopmob.smob.data.local.dto.SmobItemDTO

/**
 * Data Access Object for the smobItems table.
 */
@Dao
interface SmobItemDao {

    /**
     * @return all smobItems.
     */
    @Query("SELECT * FROM smobItems")
    suspend fun getSmobItems(): List<SmobItemDTO>

    /**
     * @param smobItemId the id of the smob item
     * @return the smob item object with the smobItemId
     */
    @Query("SELECT * FROM smobItems where entry_id = :smobItemId")
    suspend fun getSmobItemById(smobItemId: String): SmobItemDTO?

    /**
     * Insert a smob item in the database. If the smob item already exists, replace it.
     *
     * @param smobItem the smob item to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSmobItem(smobItem: SmobItemDTO)

    /**
     * Delete all smobItems.
     */
    @Query("DELETE FROM smobItems")
    suspend fun deleteAllSmobItems()

}