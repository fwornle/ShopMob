package com.tanfra.shopmob.smob.data.local.dataSource

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.dto.SmobGroupDTO
import kotlinx.coroutines.flow.Flow

/**
 * Interface for local Data Access Object for the smobGroups table.
 */
@Dao
interface SmobGroupLocalDataSource: SmobItemLocalDataSource<SmobGroupDTO> {

    /**
     * @param smobItemId the ID of the smob group
     * @return the smob group object with the smobItemId - or null if the table is empty
     */
    @Query("SELECT * FROM smobGroups WHERE groupId = :smobItemId")
    override fun getSmobItemById(smobItemId: String): Flow<SmobGroupDTO?>

    /**
     * @return all smobGroups - returns an empty list, if the table is empty
     */
    @Query("SELECT * FROM smobGroups")
    override fun getSmobItems(): Flow<List<SmobGroupDTO>>

    /**
     * @return all smobGroups on a given SmobList (= referenced groups).
     */
    @Query("SELECT * FROM smobGroups INNER JOIN smobLists ON smobLists.listId=:listId AND smobLists.listGroups LIKE '%' || smobGroups.groupId  || '%' ORDER BY smobGroups.groupName ASC, smobGroups.groupItemStatus")
    fun getSmobGroupsByListId(listId: String): Flow<List<SmobGroupDTO>>

    /**
     * Insert a smob group in the database. If the smob group already exists, replace it.
     *
     * @param smobItem the smob group to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun saveSmobItem(smobItem: SmobGroupDTO)

    /**
     * Update an existing smob group in the database. If the smob group already exists, replace it.
     * If not, do nothing.
     *
     * @param smobItem the smob group to be updated
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun updateSmobItem(smobItem: SmobGroupDTO)

    /**
     * Delete a smob group in the database.
     *
     * @param smobItemId the ID of the smob group
     */
    @Query("DELETE FROM smobGroups WHERE groupId = :smobItemId")
    override suspend fun deleteSmobItemById(smobItemId: String)

    /**
     * Delete all smob groups
     */
    @Query("DELETE FROM smobGroups")
    override suspend fun deleteAllSmobItems()

}