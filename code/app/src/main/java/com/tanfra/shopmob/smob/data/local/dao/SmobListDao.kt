package com.tanfra.shopmob.smob.data.local.dao

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.dto.SmobListDTO

/**
 * Data Access Object for the smobLists table.
 */
@Dao
interface SmobListDao {

    /**
     * @param smobListId the ID of the smob list
     * @return the smob list object with the smobListId
     */
    @Query("SELECT * FROM smobLists WHERE id = :smobListId")
    suspend fun getSmobListById(smobListId: String): SmobListDTO?

    /**
     * @return all smobLists.
     */
    @Query("SELECT * FROM smobLists")
    suspend fun getSmobLists(): List<SmobListDTO>

    /**
     * Insert a smob list in the database. If the smob list already exists, replace it.
     *
     * @param smobList the smob list to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSmobList(smobList: SmobListDTO)

    /**
     * Update an existing smob list in the database. If the smob list already exists, replace it.
     * If not, do nothing.
     *
     * @param smobList the smob list to be updated
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSmobList(smobList: SmobListDTO)

    /**
     * Delete a smob list in the database.
     *
     * @param smobListId the ID of the smob list
     */
    @Query("DELETE FROM smobLists WHERE id = :smobListId")
    suspend fun deleteSmobListById(smobListId: String)

    // Delete all smobLists.
    @Query("DELETE FROM smobLists")
    suspend fun deleteAllSmobLists()

}