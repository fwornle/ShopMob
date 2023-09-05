package com.tanfra.shopmob.smob.data.net.dataSource

import com.tanfra.shopmob.smob.data.net.nto.SmobUserNTO


/**
 * Concrete network service interface for "SmobUser" items (CRUD).
 */
interface SmobUserRemoteDataSource {

        suspend fun getSmobItemById(id: String): Result<SmobUserNTO>
        suspend fun getSmobItems(): Result<ArrayList<SmobUserNTO>>
        suspend fun saveSmobItem(newItem: SmobUserNTO): Result<Void>
        suspend fun updateSmobItemById(id: String, newItem: SmobUserNTO): Result<Void>
        suspend fun deleteSmobItemById(id: String): Result<Void>

}
