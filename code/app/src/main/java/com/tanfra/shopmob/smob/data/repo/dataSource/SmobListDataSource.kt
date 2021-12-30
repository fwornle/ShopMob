package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO

/**
 * Main entry point for accessing smob list data.
 * ... using domain data types (to abstract details of the underlying persistent storage)
 * ... wrapping results in Resource type (w/h state SUCCESS, ERROR, LOADING)
 */
interface SmobListDataSource {
    suspend fun getSmobList(id: String): Resource<SmobListATO>
    suspend fun getAllSmobLists(): Resource<List<SmobListATO>>
    suspend fun saveSmobList(smobListATO: SmobListATO)
    suspend fun saveSmobLists(smobListsATO: List<SmobListATO>)
    suspend fun updateSmobList(smobListATO: SmobListATO)
    suspend fun updateSmobLists(smobListsATO: List<SmobListATO>)
    suspend fun deleteSmobList(id: String)
    suspend fun deleteAllSmobLists()
    suspend fun refreshSmobListDataInDB()
}