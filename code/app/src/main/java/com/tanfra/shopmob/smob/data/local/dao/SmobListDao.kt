package com.tanfra.shopmob.smob.data.local.dao

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.dto.SmobGroupDTO
import com.tanfra.shopmob.smob.data.local.dto.SmobListDTO
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the smobLists table.
 */
@Dao
interface SmobListDao: SmobItemDao<SmobListDTO> {

    /**
     * @param smobItemId the ID of the smob list
     * @return the smob list object with the smobItemId
     */
    //
    // note: Flow types must not be declared as "suspend"able functions, see the third answer in:
    //       https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth
    @Query("SELECT * FROM smobLists WHERE listId = :smobItemId")
    override fun getSmobItemById(smobItemId: String): Flow<SmobListDTO?>

    /**
     * @return all smobLists.
     */
    //
    // note: Flow types must not be declared as "suspend"able functions, see the third answer in:
    //       https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth
    @Query("SELECT * FROM smobLists")
    override fun getSmobItems(): Flow<List<SmobListDTO>>

    /**
     * Insert a smob list in the database. If the smob list already exists, replace it.
     *
     * @param smobItem the smob list to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun saveSmobItem(smobItem: SmobListDTO)

    /**
     * Update an existing smob list in the database. If the smob list already exists, replace it.
     * If not, do nothing.
     *
     * @param smobItem the smob list to be updated
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun updateSmobItem(smobItem: SmobListDTO)

    /**
     * Delete a smob list in the database.
     *
     * @param smobItemId the ID of the smob list
     */
    @Query("DELETE FROM smobLists WHERE listId = :smobItemId")
    override suspend fun deleteSmobItemById(smobItemId: String)

    // Delete all smobLists.
    @Query("DELETE FROM smobLists")
    override suspend fun deleteAllSmobItems()

}