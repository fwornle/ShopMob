package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import kotlinx.coroutines.flow.Flow

/**
 * Main entry point for accessing smob product data.
 * ... using domain data types (to abstract details of the underlying persistent storage)
 * ... wrapping results in Resource type (w/h state SUCCESS, ERROR, LOADING)
 */
interface SmobProductDataSource {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    fun getSmobProduct(id: String): Flow<Resource<SmobProductATO?>>
    fun getAllSmobProducts(): Flow<Resource<List<SmobProductATO>>>

    // By default Room runs suspend queries off the main thread
    suspend fun saveSmobProduct(smobProductATO: SmobProductATO)
    suspend fun saveSmobProducts(smobProductsATO: List<SmobProductATO>)
    suspend fun updateSmobProduct(smobProductATO: SmobProductATO)
    suspend fun updateSmobProducts(smobProductsATO: List<SmobProductATO>)
    suspend fun deleteSmobProduct(id: String)
    suspend fun deleteAllSmobProducts()
    suspend fun refreshDataInLocalDB()

}