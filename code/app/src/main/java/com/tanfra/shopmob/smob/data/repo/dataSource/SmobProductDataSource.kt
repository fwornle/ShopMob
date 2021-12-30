package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.types.SmobProduct

/**
 * Main entry point for accessing smob product data.
 */
interface SmobProductDataSource {
    suspend fun getSmobProducts(): Resource<List<SmobProduct>>
    suspend fun saveSmobProduct(smobProduct: SmobProduct)
    suspend fun getSmobProduct(id: String): Resource<SmobProduct>
    suspend fun deleteAllSmobProducts()
}