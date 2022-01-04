package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO
import kotlinx.coroutines.flow.Flow

/**
 * Main entry point for accessing smob user data.
 * ... using domain data types (to abstract details of the underlying persistent storage)
 * ... wrapping results in Resource type (w/h state SUCCESS, ERROR, LOADING)
 */
interface SmobUserDataSource {
    suspend fun getSmobUser(id: String): Flow<Resource<SmobUserATO?>>
    suspend fun getAllSmobUsers(): Flow<Resource<List<SmobUserATO>>>
    suspend fun saveSmobUser(smobUserATO: SmobUserATO)
    suspend fun saveSmobUsers(smobUsersATO: List<SmobUserATO>)
    suspend fun updateSmobUser(smobUserATO: SmobUserATO)
    suspend fun updateSmobUsers(smobUsersATO: List<SmobUserATO>)
    suspend fun deleteSmobUser(id: String)
    suspend fun deleteAllSmobUsers()
    suspend fun refreshDataInLocalDB()
}