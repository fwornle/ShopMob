package com.tanfra.shopmob.smob.data.local.dao

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.dto.SmobGroupDTO
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the smobGroups table.
 */
@Dao
interface SmobGroupDao {

    /**
     * @param smobGroupId the ID of the smob group
     * @return the smob group object with the smobGroupId
     */
    //
    // note: Flow types must not be declared as "suspend"able functions, see the third answer in:
    //       https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth
    @Query("SELECT * FROM smobGroups WHERE groupId = :smobGroupId")
    fun getSmobGroupById(smobGroupId: String): Flow<SmobGroupDTO?>

    /**
     * @return all smobGroups.
     */
    //
    // note: Flow types must not be declared as "suspend"able functions, see the third answer in:
    //       https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth
    @Query("SELECT * FROM smobGroups")
    fun getSmobGroups(): Flow<List<SmobGroupDTO>>

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
    @Query("DELETE FROM smobGroups WHERE groupId = :smobGroupId")
    suspend fun deleteSmobGroupById(smobGroupId: String)

    // Delete all smobGroups.
    @Query("DELETE FROM smobGroups")
    suspend fun deleteAllSmobGroups()

}