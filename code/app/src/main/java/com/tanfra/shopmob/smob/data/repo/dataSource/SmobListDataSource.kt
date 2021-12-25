package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.Result
import com.tanfra.shopmob.smob.types.SmobList

/**
 * Main entry point for accessing smob list data.
 */
interface SmobListDataSource {
    suspend fun getSmobLists(): Result<List<SmobList>>
    suspend fun saveSmobList(smobList: SmobList)
    suspend fun getSmobList(id: String): Result<SmobList>
    suspend fun deleteAllSmobLists()
}