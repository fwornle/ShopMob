package com.tanfra.shopmob.smob.data.local.dao

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.dto.SmobItemDTO
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the smobItems table.
 */
@Dao
interface SmobItemDao {

    /**
     * @param smobItemId the ID of the smob item
     * @return the smob item object with the smobItemId
     */
    @Query("SELECT * FROM smobItems WHERE id = :smobItemId")
    suspend fun getSmobItemById(smobItemId: String): SmobItemDTO?

    /**
     * @return all smobItems.
     */
    @Query("SELECT * FROM smobItems")
    suspend fun getSmobItems(): List<SmobItemDTO>

    /**
     * Insert a smob item in the database. If the smob item already exists, replace it.
     *
     * @param smobItem the smob item to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSmobItem(smobItem: SmobItemDTO)

    /**
     * Update an existing smob item in the database. If the smob item already exists, replace it.
     * If not, do nothing.
     *
     * @param smobItem the smob item to be updated
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSmobItem(smobItem: SmobItemDTO)

    /**
     * Delete a smob item in the database.
     *
     * @param smobItemId the ID of the smob item
     */
    @Query("DELETE FROM smobItems WHERE id = :smobItemId")
    suspend fun deleteSmobItemById(smobItemId: String)

    // Delete all smobItems.
    @Query("DELETE FROM smobItems")
    suspend fun deleteAllSmobItems()

}