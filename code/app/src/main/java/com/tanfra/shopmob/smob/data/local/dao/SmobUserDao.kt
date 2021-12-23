package com.tanfra.shopmob.smob.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tanfra.shopmob.smob.data.local.dto.SmobUserDTO

/**
 * Data Access Object for the smobUsers table.
 */
@Dao
interface SmobUserDao {

    /**
     * @return all smobUsers.
     */
    @Query("SELECT * FROM smobUsers")
    suspend fun getSmobUsers(): List<SmobUserDTO>

    /**
     * @param smobUserId the id of the smob user
     * @return the smob user object with the smobUserId
     */
    @Query("SELECT * FROM smobUsers where id = :smobUserId")
    suspend fun getSmobUserById(smobUserId: String): SmobUserDTO?

    /**
     * Insert a smob user in the database. If the smob user already exists, replace it.
     *
     * @param smobUser the smob user to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSmobUser(smobUser: SmobUserDTO)

    /**
     * Delete all smobUsers.
     */
    @Query("DELETE FROM smobUsers")
    suspend fun deleteAllSmobUsers()

}