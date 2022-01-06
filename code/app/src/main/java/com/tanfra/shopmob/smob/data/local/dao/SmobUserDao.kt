package com.tanfra.shopmob.smob.data.local.dao

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.dto.SmobUserDTO
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the smobUsers table.
 */
@Dao
interface SmobUserDao {

    /**
     * @param smobUserId the ID of the smob user
     * @return the smob user object with the smobUserId
     */
    //
    // note: Flow types must not be declared as "suspend"able functions, see the third answer in:
    //       https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth
    @Query("SELECT * FROM smobUsers WHERE userId = :smobUserId")
    fun getSmobUserById(smobUserId: String): Flow<SmobUserDTO?>

    /**
     * @return all smobUsers.
     */
    //
    // note: Flow types must not be declared as "suspend"able functions, see the third answer in:
    //       https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth
    @Query("SELECT * FROM smobUsers")
    fun getSmobUsers(): Flow<List<SmobUserDTO>>

    /**
     * Insert a smob user in the database. If the smob user already exists, replace it.
     *
     * @param smobUser the smob user to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSmobUser(smobUser: SmobUserDTO)

    /**
     * Update an existing smob user in the database. If the smob user already exists, replace it.
     * If not, do nothing.
     *
     * @param smobUser the smob user to be updated
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSmobUser(smobUser: SmobUserDTO)

    /**
     * Delete a smob user in the database.
     *
     * @param smobUserId the ID of the smob user
     */
    @Query("DELETE FROM smobUsers WHERE userId = :smobUserId")
    suspend fun deleteSmobUserById(smobUserId: String)

    // Delete all smobUsers.
    @Query("DELETE FROM smobUsers")
    suspend fun deleteAllSmobUsers()

}