package com.tanfra.shopmob.smob.data.local.dao

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.dto.SmobProductDTO
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the smobProducts table.
 */
@Dao
interface SmobProductDao {

    /**
     * @param smobProductId the ID of the smob product
     * @return the smob product object with the smobProductId
     */
    //
    // note: Flow types must not be declared as "suspend"able functions, see the third answer in:
    //       https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth
    @Query("SELECT * FROM smobProducts WHERE id = :smobProductId")
    fun getSmobProductById(smobProductId: String): Flow<SmobProductDTO?>

    /**
     * @return all smobProducts.
     */
    //
    // note: Flow types must not be declared as "suspend"able functions, see the third answer in:
    //       https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth
    @Query("SELECT * FROM smobProducts")
    fun getSmobProducts(): Flow<List<SmobProductDTO>>

    /**
     * Insert a smob product in the database. If the smob product already exists, replace it.
     *
     * @param smobProduct the smob product to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSmobProduct(smobProduct: SmobProductDTO)

    /**
     * Update an existing smob product in the database. If the smob product already exists, replace it.
     * If not, do nothing.
     *
     * @param smobProduct the smob product to be updated
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSmobProduct(smobProduct: SmobProductDTO)

    /**
     * Delete a smob product in the database.
     *
     * @param smobProductId the ID of the smob product
     */
    @Query("DELETE FROM smobProducts WHERE id = :smobProductId")
    suspend fun deleteSmobProductById(smobProductId: String)

    // Delete all smobProducts.
    @Query("DELETE FROM smobProducts")
    suspend fun deleteAllSmobProducts()

}