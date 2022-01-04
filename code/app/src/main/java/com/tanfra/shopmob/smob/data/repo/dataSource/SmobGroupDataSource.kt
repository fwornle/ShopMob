package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupATO
import kotlinx.coroutines.flow.Flow

/**
 * Main entry point for accessing smob group data.
 * ... using domain data types (to abstract details of the underlying persistent storage)
 * ... wrapping results in Resource type (w/h state SUCCESS, ERROR, LOADING)
 */
interface SmobGroupDataSource {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    fun getSmobGroup(id: String): Flow<Resource<SmobGroupATO?>>
    fun getAllSmobGroups(): Flow<Resource<List<SmobGroupATO>>>

    // By default Room runs suspend queries off the main thread
    suspend fun saveSmobGroup(smobGroupATO: SmobGroupATO)
    suspend fun saveSmobGroups(smobGroupsATO: List<SmobGroupATO>)
    suspend fun updateSmobGroup(smobGroupATO: SmobGroupATO)
    suspend fun updateSmobGroups(smobGroupsATO: List<SmobGroupATO>)
    suspend fun deleteSmobGroup(id: String)
    suspend fun deleteAllSmobGroups()
    suspend fun refreshDataInLocalDB()

}