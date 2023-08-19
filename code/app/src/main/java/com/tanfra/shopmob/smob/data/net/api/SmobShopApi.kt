package com.tanfra.shopmob.smob.data.net.api

import com.tanfra.shopmob.smob.data.net.nto.SmobShopNTO


/**
 * Concrete network service interface for "SmobShop" items (CRUD).
 */
interface SmobShopApi {

        suspend fun getSmobItemById(id: String): Result<SmobShopNTO>
        suspend fun getSmobItems(): Result<ArrayList<SmobShopNTO>>
        suspend fun saveSmobItem(newItem: SmobShopNTO): Result<Void>
        suspend fun updateSmobItemById(id: String, newItem: SmobShopNTO): Result<Void>
        suspend fun deleteSmobItemById(id: String): Result<Void>

}
