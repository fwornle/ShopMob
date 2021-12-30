package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.types.SmobUser

/**
 * Main entry point for accessing smob user data.
 * ... using domain data types (to abstract details of the underlying persistent storage)
 * ... wrapping results in Resource type (w/h state SUCCESS, ERROR, LOADING)
 */
interface SmobUserDataSource {
    suspend fun getSmobUser(id: String): Resource<SmobUser>
    suspend fun getAllSmobUsers(): Resource<List<SmobUser>>
    suspend fun saveSmobUser(smobUser: SmobUser)
    suspend fun saveSmobUsers(smobUsers: List<SmobUser>)
    suspend fun updateSmobUser(smobUser: SmobUser)
    suspend fun updateSmobUsers(smobUsers: List<SmobUser>)
    suspend fun deleteSmobUser(id: String)
    suspend fun deleteAllSmobUsers()
    suspend fun refreshSmobUserDataInDB()
}