package com.tanfra.shopmob.smob.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tanfra.shopmob.smob.data.local.dto.SmobListDTO

/**
 * Data Access Object for the smobLists table.
 */
@Dao
interface SmobListDao {

    /**
     * @return all smobLists.
     */
    @Query("SELECT * FROM smobLists")
    suspend fun getSmobLists(): List<SmobListDTO>

    /**
     * @param smobListId the id of the smob List
     * @return the smob List object with the smobListId
     */
    @Query("SELECT * FROM smobLists where id = :smobListId")
    suspend fun getSmobListById(smobListId: String): SmobListDTO?

    /**
     * Insert a smob List in the database. If the smob List already exists, replace it.
     *
     * @param smobList the smob List to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSmobList(smobList: SmobListDTO)

    /**
     * Delete all smobLists.
     */
    @Query("DELETE FROM smobLists")
    suspend fun deleteAllSmobLists()

}