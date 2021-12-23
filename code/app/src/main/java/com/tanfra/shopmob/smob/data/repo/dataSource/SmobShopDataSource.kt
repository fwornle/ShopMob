package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.Result
import com.tanfra.shopmob.smob.types.SmobShop

/**
 * Main entry point for accessing smob shop data.
 */
interface SmobShopDataSource {
    suspend fun getSmobShops(): Result<List<SmobShop>>
    suspend fun saveSmobShop(smobShop: SmobShop)
    suspend fun getSmobShop(id: String): Result<SmobShop>
    suspend fun deleteAllSmobShops()
}