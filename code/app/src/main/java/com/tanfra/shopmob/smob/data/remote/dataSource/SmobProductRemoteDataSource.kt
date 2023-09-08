package com.tanfra.shopmob.smob.data.remote.dataSource

import com.tanfra.shopmob.smob.data.remote.nto.SmobProductNTO


/**
 * Concrete network service interface for "SmobProduct" items (CRUD).
 */
interface SmobProductRemoteDataSource {

        suspend fun getSmobItemById(id: String): Result<SmobProductNTO>
        suspend fun getSmobItems(): Result<ArrayList<SmobProductNTO>>

        suspend fun saveSmobItem(newItem: SmobProductNTO): Result<Void>
        suspend fun updateSmobItemById(id: String, newItem: SmobProductNTO): Result<Void>
        suspend fun deleteSmobItemById(id: String): Result<Void>

}
