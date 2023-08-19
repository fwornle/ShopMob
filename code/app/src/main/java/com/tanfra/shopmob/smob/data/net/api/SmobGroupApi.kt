package com.tanfra.shopmob.smob.data.net.api

import com.tanfra.shopmob.smob.data.net.nto.SmobGroupNTO


/**
 * Concrete network service interface for "SmobGroup" items (CRUD).
 */
interface SmobGroupApi {

        suspend fun getSmobItemById(id: String): Result<SmobGroupNTO>
        suspend fun getSmobItems(): Result<ArrayList<SmobGroupNTO>>
        suspend fun saveSmobItem(newItem: SmobGroupNTO): Result<Void>
        suspend fun updateSmobItemById(id: String, newItem: SmobGroupNTO): Result<Void>
        suspend fun deleteSmobItemById(id: String): Result<Void>

}
