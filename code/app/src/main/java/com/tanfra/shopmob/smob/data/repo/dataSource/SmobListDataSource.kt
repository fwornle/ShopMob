package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO
import kotlinx.coroutines.flow.Flow

/**
 * Main entry point for accessing smob list data.
 * ... using domain data types (to abstract details of the underlying persistent storage)
 * ... wrapping results in Resource type (w/h state SUCCESS, ERROR, LOADING)
 */
interface SmobListDataSource {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    fun getSmobList(id: String): Flow<Resource<SmobListATO?>>
    fun getAllSmobLists(): Flow<Resource<List<SmobListATO>>>

    // By default Room runs suspend queries off the main thread
    suspend fun saveSmobList(smobListATO: SmobListATO)
    suspend fun saveSmobLists(smobListsATO: List<SmobListATO>)
    suspend fun updateSmobList(smobListATO: SmobListATO)
    suspend fun updateSmobLists(smobListsATO: List<SmobListATO>)
    suspend fun deleteSmobList(id: String)
    suspend fun deleteAllSmobLists()
    suspend fun refreshDataInLocalDB()
    suspend fun refreshSmobListInLocalDB(id: String)
    suspend fun refreshSmobListInRemoteDB(smobListATO: SmobListATO)

}