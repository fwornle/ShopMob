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
    //
    // note: Flow types must not be declared as "suspend"able functions, see the third answer in:
    //       https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth
    @Query("SELECT * FROM smobShops WHERE shopId = :smobShopId")
    fun getSmobShopById(smobShopId: String): Flow<SmobShopDTO?>

    /**
     * @return all smobShops.
     */
    //
    // note: Flow types must not be declared as "suspend"able functions, see the third answer in:
    //       https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth
    @Query("SELECT * FROM smobShops")
    fun getSmobShops(): Flow<List<SmobShopDTO>>

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
    @Query("DELETE FROM smobShops WHERE shopId = :smobShopId")
    suspend fun deleteSmobShopById(smobShopId: String)

    // Delete all smobShops.
    @Query("DELETE FROM smobShops")
    suspend fun deleteAllSmobShops()

}