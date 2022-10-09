package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import kotlinx.coroutines.flow.Flow

/**
 * Main entry point for accessing smob shop data.
 * ... using domain data types (to abstract details of the underlying persistent storage)
 * ... wrapping results in Resource type (w/h state SUCCESS, ERROR, LOADING)
 */
interface SmobShopDataSource : SmobItemDataSource<SmobShopATO>{

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    override fun getSmobItem(id: String): Flow<Resource<SmobShopATO?>>
    override fun getAllSmobItems(): Flow<Resource<List<SmobShopATO>>>

    // By default Room runs suspend queries off the main thread
    override suspend fun saveSmobItem(smobItemATO: SmobShopATO)
    override suspend fun saveSmobItems(smobItemsATO: List<SmobShopATO>)
    override suspend fun updateSmobItem(smobItemATO: SmobShopATO)
    override suspend fun updateSmobItems(smobItemsATO: List<SmobShopATO>)
    override suspend fun deleteSmobItem(id: String)
    override suspend fun deleteAllSmobItems()
    override suspend fun refreshDataInLocalDB()
    override suspend fun refreshSmobItemInLocalDB(id: String)

}