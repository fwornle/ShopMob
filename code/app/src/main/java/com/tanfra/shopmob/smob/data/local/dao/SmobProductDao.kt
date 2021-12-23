package com.tanfra.shopmob.smob.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tanfra.shopmob.smob.data.local.dto.SmobProductDTO

/**
 * Data Access Object for the smobProducts table.
 */
@Dao
interface SmobProductDao {

    /**
     * @return all smobProducts.
     */
    @Query("SELECT * FROM smobProducts")
    suspend fun getSmobProducts(): List<SmobProductDTO>

    /**
     * @param smobProductId the id of the smob product
     * @return the smob product object with the smobProductId
     */
    @Query("SELECT * FROM smobProducts where id = :smobProductId")
    suspend fun getSmobProductById(smobProductId: String): SmobProductDTO?

    /**
     * Insert a smob product in the database. If the smob product already exists, replace it.
     *
     * @param smobProduct the smob product to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSmobProduct(smobProduct: SmobProductDTO)

    /**
     * Delete all smobProducts.
     */
    @Query("DELETE FROM smobProducts")
    suspend fun deleteAllSmobProducts()

}