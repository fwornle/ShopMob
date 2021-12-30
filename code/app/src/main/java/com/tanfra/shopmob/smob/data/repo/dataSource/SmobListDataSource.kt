package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.types.SmobList

/**
 * Main entry point for accessing smob list data.
 */
interface SmobListDataSource {
    suspend fun getSmobLists(): Resource<List<SmobList>>
    suspend fun saveSmobList(smobList: SmobList)
    suspend fun getSmobList(id: String): Resource<SmobList>
    suspend fun deleteAllSmobLists()
}