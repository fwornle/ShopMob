package com.tanfra.shopmob.smob.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tanfra.shopmob.smob.data.local.dto.SmobShopDTO

/**
 * Data Access Object for the smobShops table.
 */
@Dao
interface SmobShopDao {

    /**
     * @return all smobShops.
     */
    @Query("SELECT * FROM smobShops")
    suspend fun getSmobShops(): List<SmobShopDTO>

    /**
     * @param smobShopId the id of the smob shop
     * @return the smob shop object with the smobShopId
     */
    @Query("SELECT * FROM smobShops where id = :smobShopId")
    suspend fun getSmobShopById(smobShopId: String): SmobShopDTO?

    /**
     * Insert a smob shop in the database. If the smob shop already exists, replace it.
     *
     * @param smobShop the smob shop to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSmobShop(smobShop: SmobShopDTO)

    /**
     * Delete all smobShops.
     */
    @Query("DELETE FROM smobShops")
    suspend fun deleteAllSmobShops()

}