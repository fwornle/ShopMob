package com.tanfra.shopmob.smob.data.local.dao

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.dto.SmobGroupDTO
import com.tanfra.shopmob.smob.data.local.dto.SmobProductDTO
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the smobProducts table.
 */
@Dao
interface SmobProductDao: SmobItemDao<SmobProductDTO> {

    /**
     * @param smobItemId the ID of the smob product
     * @return the smob product object with the smobItemId
     */
    //
    // note: Flow types must not be declared as "suspend"able functions, see the third answer in:
    //       https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth
    @Query("SELECT * FROM smobProducts WHERE productId = :smobItemId")
    override fun getSmobItemById(smobItemId: String): Flow<SmobProductDTO?>

    /**
     * @return all smobProducts.
     */
    //
    // note: Flow types must not be declared as "suspend"able functions, see the third answer in:
    //       https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth
    @Query("SELECT * FROM smobProducts")
    override fun getSmobItems(): Flow<List<SmobProductDTO>>

    /**
     * @return all smobProducts.
     */
    //
    // Notes:
    //
    // - Flow types must not be declared as "suspend"able functions, see the third answer in:
    //   https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth
    //
    // - using INNER JOIN to resolve the data dependency between lists and products at DB level
    //   ... this is necessary to be able to work with Flow<List<SmobProductDTO>> within the app, as
    //   there is no way to transform a Flow<SmobProductDTO> into a Flow<List<SmobProductDTO>>
    //   without first collecting the individual product flows and re-assembling a (then pointless)
    //   flow of List<SmobProductDTO>
    // - using LIKE to perform a regex search on field 'smobLists.items', as this is a JSON encoded
    //   serialization of the underlying List<SmobListItem>
    //
    @Query("SELECT * FROM smobProducts INNER JOIN smobLists ON smobLists.listId=:listId AND smobLists.listItems LIKE '%' || smobProducts.productId  || '%' ORDER BY smobProducts.productCategoryMain ASC, smobProducts.productCategorySub")
    fun getSmobProductsByListId(listId: String): Flow<List<SmobProductDTO>>

    /**
     * Insert a smob product in the database. If the smob product already exists, replace it.
     *
     * @param smobItem the smob product to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun saveSmobItem(smobItem: SmobProductDTO)

    /**
     * Update an existing smob product in the database. If the smob product already exists, replace it.
     * If not, do nothing.
     *
     * @param smobItem the smob product to be updated
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun updateSmobItem(smobItem: SmobProductDTO)

    /**
     * Delete a smob product in the database.
     *
     * @param smobItemId the ID of the smob product
     */
    @Query("DELETE FROM smobProducts WHERE productId = :smobItemId")
    override suspend fun deleteSmobItemById(smobItemId: String)

    // Delete all smobProducts.
    @Query("DELETE FROM smobProducts")
    override suspend fun deleteAllSmobItems()

}