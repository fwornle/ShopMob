package com.tanfra.shopmob.smob.data.local.dao

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.dto.SmobUserDTO

/**
 * Data Access Object for the smobUsers table.
 */
@Dao
interface SmobUserDao {

    /**
     * @param smobUserId the ID of the smob user
     * @return the smob user object with the smobUserId
     */
    @Query("SELECT * FROM smobUsers WHERE id = :smobUserId")
    suspend fun getSmobUserById(smobUserId: String): SmobUserDTO?

    /**
     * @return all smobUsers.
     */
    @Query("SELECT * FROM smobUsers")
    suspend fun getSmobUsers(): List<SmobUserDTO>

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
    @Query("DELETE FROM smobUsers WHERE id = :smobUserId")
    suspend fun deleteSmobUserById(smobUserId: String)

    // Delete all smobUsers.
    @Query("DELETE FROM smobUsers")
    suspend fun deleteAllSmobUsers()

}