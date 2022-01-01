package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO

/**
 * Main entry point for accessing smob product data.
 * ... using domain data types (to abstract details of the underlying persistent storage)
 * ... wrapping results in Resource type (w/h state SUCCESS, ERROR, LOADING)
 */
interface SmobProductDataSource {
    suspend fun getSmobProduct(id: String): Resource<SmobProductATO>
    suspend fun getAllSmobProducts(): Resource<List<SmobProductATO>>
    suspend fun saveSmobProduct(smobProductATO: SmobProductATO)
    suspend fun saveSmobProducts(smobProductsATO: List<SmobProductATO>)
    suspend fun updateSmobProduct(smobProductATO: SmobProductATO)
    suspend fun updateSmobProducts(smobProductsATO: List<SmobProductATO>)
    suspend fun deleteSmobProduct(id: String)
    suspend fun deleteAllSmobProducts()
    suspend fun refreshSmobProductsInDB()
}