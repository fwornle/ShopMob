package com.tanfra.shopmob.smob.data.local.dao

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.dto.SmobGroupDTO

/**
 * Data Access Object for the smobGroups table.
 */
@Dao
interface SmobGroupDao {

    /**
     * @param smobGroupId the ID of the smob group
     * @return the smob group object with the smobGroupId
     */
    @Query("SELECT * FROM smobGroups WHERE id = :smobGroupId")
    suspend fun getSmobGroupById(smobGroupId: String): SmobGroupDTO?

    /**
     * @return all smobGroups.
     */
    @Query("SELECT * FROM smobGroups")
    suspend fun getSmobGroups(): List<SmobGroupDTO>

    /**
     * Insert a smob group in the database. If the smob group already exists, replace it.
     *
     * @param smobGroup the smob group to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSmobGroup(smobGroup: SmobGroupDTO)

    /**
     * Update an existing smob group in the database. If the smob group already exists, replace it.
     * If not, do nothing.
     *
     * @param smobGroup the smob group to be updated
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSmobGroup(smobGroup: SmobGroupDTO)

    /**
     * Delete a smob group in the database.
     *
     * @param smobGroupId the ID of the smob group
     */
    @Query("DELETE FROM smobGroups WHERE id = :smobGroupId")
    suspend fun deleteSmobGroupById(smobGroupId: String)

    // Delete all smobGroups.
    @Query("DELETE FROM smobGroups")
    suspend fun deleteAllSmobGroups()

}