package com.tanfra.shopmob.smob.data.net.api

import com.tanfra.shopmob.smob.data.net.nto.SmobListNTO


/**
 * Concrete network service interface for "SmobList" items (CRUD).
 */
interface SmobListApi {

        suspend fun getSmobItemById(id: String): Result<SmobListNTO>
        suspend fun getSmobItems(): Result<ArrayList<SmobListNTO>>
        suspend fun saveSmobItem(newItem: SmobListNTO): Result<Void>
        suspend fun updateSmobItemById(id: String, newItem: SmobListNTO): Result<Void>
        suspend fun deleteSmobItemById(id: String): Result<Void>

}
