package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.local.dto.SmobUserDTO
import com.tanfra.shopmob.smob.types.SmobUser
import com.tanfra.shopmob.smob.data.repo.Result

/**
 * Main entry point for accessing smob user data.
 * ... using domain data types (to abstract details of the underlying persistent storage)
 */
interface SmobUserDataSource {
    suspend fun getSmobUser(id: String): Result<SmobUser>
    suspend fun getAllSmobUsers(): Result<List<SmobUser>>
    suspend fun saveSmobUser(smobUser: SmobUser)
    suspend fun saveSmobUsers(smobUsers: List<SmobUser>)
    suspend fun updateSmobUser(smobUser: SmobUser)
    suspend fun updateSmobUsers(smobUsers: List<SmobUser>)
    suspend fun deleteSmobUser(id: String)
    suspend fun deleteAllSmobUsers()
    suspend fun refreshSmobUserDataInDB()
}