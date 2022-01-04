package com.tanfra.shopmob.smob.data.local.dao

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.dto.SmobListDTO
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the smobLists table.
 */
@Dao
interface SmobListDao {

    /**
     * @param smobListId the ID of the smob list
     * @return the smob list object with the smobListId
     */
    //
    // note: Flow types must not be declared as "suspend"able functions, see the third answer in:
    //       https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth
    @Query("SELECT * FROM smobLists WHERE id = :smobListId")
    fun getSmobListById(smobListId: String): Flow<SmobListDTO?>

    /**
     * @return all smobLists.
     */
    //
    // note: Flow types must not be declared as "suspend"able functions, see the third answer in:
    //       https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth
    @Query("SELECT * FROM smobLists")
    fun getSmobLists(): Flow<List<SmobListDTO>>

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