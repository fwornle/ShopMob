package com.tanfra.shopmob.smob.data.net.dataSource

import com.tanfra.shopmob.smob.data.net.nto.Nto

/**
 * Generic network service interface for "SmobItems" (CRUD).
 */
interface SmobItemRemoteDataSource<NTO: Nto> {

        suspend fun getSmobItemById(id: String): Result<NTO>
        suspend fun getSmobItems(): Result<ArrayList<NTO>>
        suspend fun saveSmobItem(newItem: NTO): Result<Void>
        suspend fun updateSmobItemById(id: String, newItem: NTO): Result<Void>
        suspend fun deleteSmobItemById(id: String): Result<Void>

}
