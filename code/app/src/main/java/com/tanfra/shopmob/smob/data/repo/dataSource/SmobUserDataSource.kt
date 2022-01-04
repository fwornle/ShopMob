package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Main entry point for accessing smob user data.
 * ... using domain data types (to abstract details of the underlying persistent storage)
 * ... wrapping results in Resource type (w/h state SUCCESS, ERROR, LOADING)
 */
interface SmobUserDataSource {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    fun getSmobUser(id: String): Flow<Resource<SmobUserATO?>>
    fun getAllSmobUsers(): Flow<Resource<List<SmobUserATO>>>

    // By default Room runs suspend queries off the main thread
    suspend fun saveSmobUser(smobUserATO: SmobUserATO)
    suspend fun saveSmobUsers(smobUsersATO: List<SmobUserATO>)
    suspend fun updateSmobUser(smobUserATO: SmobUserATO)
    suspend fun updateSmobUsers(smobUsersATO: List<SmobUserATO>)
    suspend fun deleteSmobUser(id: String)
    suspend fun deleteAllSmobUsers()
    suspend fun refreshDataInLocalDB()

}