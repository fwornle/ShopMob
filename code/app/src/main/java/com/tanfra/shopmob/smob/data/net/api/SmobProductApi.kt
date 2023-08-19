package com.tanfra.shopmob.smob.data.net.api

import com.tanfra.shopmob.smob.data.net.nto.SmobProductNTO


/**
 * Concrete network service interface for "SmobProduct" items (CRUD).
 */
interface SmobProductApi {

        suspend fun getSmobItemById(id: String): Result<SmobProductNTO>
        suspend fun getSmobItems(): Result<ArrayList<SmobProductNTO>>
        suspend fun saveSmobItem(newItem: SmobProductNTO): Result<Void>
        suspend fun updateSmobItemById(id: String, newItem: SmobProductNTO): Result<Void>
        suspend fun deleteSmobItemById(id: String): Result<Void>

}
