package com.tanfra.shopmob.smob.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tanfra.shopmob.smob.data.local.dto.SmobGroupDTO

/**
 * Data Access Object for the smobGroups table.
 */
@Dao
interface SmobGroupDao {

    /**
     * @return all smobGroups.
     */
    @Query("SELECT * FROM smobGroups")
    suspend fun getSmobGroups(): List<SmobGroupDTO>

    /**
     * @param smobGroupId the id of the smob group
     * @return the smob group object with the smobGroupId
     */
    @Query("SELECT * FROM smobGroups where id = :smobGroupId")
    suspend fun getSmobGroupById(smobGroupId: String): SmobGroupDTO?

    /**
     * Insert a smob group in the database. If the smob group already exists, replace it.
     *
     * @param smobGroup the smob group to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSmobGroup(smobGroup: SmobGroupDTO)

    /**
     * Delete all smobGroups.
     */
    @Query("DELETE FROM smobGroups")
    suspend fun deleteAllSmobGroups()

}