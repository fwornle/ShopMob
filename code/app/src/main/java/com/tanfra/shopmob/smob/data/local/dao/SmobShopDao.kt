package com.tanfra.shopmob.smob.data.local.dao

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.dto.SmobShopDTO
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the smobShops table.
 */
@Dao
interface SmobShopDao {

    /**
     * @param smobShopId the ID of the smob shop
     * @return the smob shop object with the smobShopId
     */
    @Query("SELECT * FROM smobShops WHERE id = :smobShopId")
    suspend fun getSmobShopById(smobShopId: String): SmobShopDTO?

    /**
     * @return all smobShops.
     */
    @Query("SELECT * FROM smobShops")
    suspend fun getSmobShops(): List<SmobShopDTO>

    /**
     * Insert a smob shop in the database. If the smob shop already exists, replace it.
     *
     * @param smobShop the smob shop to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSmobShop(smobShop: SmobShopDTO)

    /**
     * Update an existing smob shop in the database. If the smob shop already exists, replace it.
     * If not, do nothing.
     *
     * @param smobShop the smob shop to be updated
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSmobShop(smobShop: SmobShopDTO)

    /**
     * Delete a smob shop in the database.
     *
     * @param smobShopId the ID of the smob shop
     */
    @Query("DELETE FROM smobShops WHERE id = :smobShopId")
    suspend fun deleteSmobShopById(smobShopId: String)

    // Delete all smobShops.
    @Query("DELETE FROM smobShops")
    suspend fun deleteAllSmobShops()

}