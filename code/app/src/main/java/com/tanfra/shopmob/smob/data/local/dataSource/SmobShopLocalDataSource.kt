package com.tanfra.shopmob.smob.data.local.dataSource

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.dto.SmobShopDTO
import kotlinx.coroutines.flow.Flow

/**
 * Interface for local Data Access Object for the smobShops table.
 */
@Dao
interface SmobShopLocalDataSource: SmobItemLocalDataSource<SmobShopDTO> {

    /**
     * @param smobItemId the ID of the smob shop
     * @return the smob shop object with the smobItemId
     */
    //
    // note: Flow types must not be declared as "suspend"able functions, see the third answer in:
    //       https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth
    @Query("SELECT * FROM smobShops WHERE shopId = :smobItemId")
    override fun getSmobItemById(smobItemId: String): Flow<SmobShopDTO?>

    /**
     * @return all smobShops.
     */
    //
    // note: Flow types must not be declared as "suspend"able functions, see the third answer in:
    //       https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth
    @Query("SELECT * FROM smobShops")
    override fun getSmobItems(): Flow<List<SmobShopDTO>>

    /**
     * Insert a smob shop in the database. If the smob shop already exists, replace it.
     *
     * @param smobItem the smob shop to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun saveSmobItem(smobItem: SmobShopDTO)

    /**
     * Update an existing smob shop in the database. If the smob shop already exists, replace it.
     * If not, do nothing.
     *
     * @param smobItem the smob shop to be updated
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun updateSmobItem(smobItem: SmobShopDTO)

    /**
     * Delete a smob shop in the database.
     *
     * @param smobItemId the ID of the smob shop
     */
    @Query("DELETE FROM smobShops WHERE shopId = :smobItemId")
    override suspend fun deleteSmobItemById(smobItemId: String)

    // Delete all smobShops.
    @Query("DELETE FROM smobShops")
    override suspend fun deleteAllSmobItems()

}