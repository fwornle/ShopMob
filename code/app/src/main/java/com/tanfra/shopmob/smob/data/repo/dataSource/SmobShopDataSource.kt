package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO

/**
 * Main entry point for accessing smob shop data.
 * ... using domain data types (to abstract details of the underlying persistent storage)
 * ... wrapping results in Resource type (w/h state SUCCESS, ERROR, LOADING)
 */
interface SmobShopDataSource {
    suspend fun getSmobShop(id: String): Resource<SmobShopATO>
    suspend fun getAllSmobShops(): Resource<List<SmobShopATO>>
    suspend fun saveSmobShop(smobShopATO: SmobShopATO)
    suspend fun saveSmobShops(smobShopsATO: List<SmobShopATO>)
    suspend fun updateSmobShop(smobShopATO: SmobShopATO)
    suspend fun updateSmobShops(smobShopsATO: List<SmobShopATO>)
    suspend fun deleteSmobShop(id: String)
    suspend fun deleteAllSmobShops()
    suspend fun refreshDataInLocalDB()
}