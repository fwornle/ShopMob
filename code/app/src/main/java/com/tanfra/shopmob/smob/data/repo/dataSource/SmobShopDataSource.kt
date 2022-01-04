package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import kotlinx.coroutines.flow.Flow

/**
 * Main entry point for accessing smob shop data.
 * ... using domain data types (to abstract details of the underlying persistent storage)
 * ... wrapping results in Resource type (w/h state SUCCESS, ERROR, LOADING)
 */
interface SmobShopDataSource {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    fun getSmobShop(id: String): Flow<Resource<SmobShopATO?>>
    fun getAllSmobShops(): Flow<Resource<List<SmobShopATO>>>

    // By default Room runs suspend queries off the main thread
    suspend fun saveSmobShop(smobShopATO: SmobShopATO)
    suspend fun saveSmobShops(smobShopsATO: List<SmobShopATO>)
    suspend fun updateSmobShop(smobShopATO: SmobShopATO)
    suspend fun updateSmobShops(smobShopsATO: List<SmobShopATO>)
    suspend fun deleteSmobShop(id: String)
    suspend fun deleteAllSmobShops()
    suspend fun refreshDataInLocalDB()

}