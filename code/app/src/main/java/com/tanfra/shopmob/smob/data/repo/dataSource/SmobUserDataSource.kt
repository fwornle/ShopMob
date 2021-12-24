package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.types.SmobUser
import com.tanfra.shopmob.smob.data.repo.Result

/**
 * Main entry point for accessing smob user data.
 * ... using domain data types (to further abstract DB details)
 */
interface SmobUserDataSource {
    suspend fun getSmobUsers(): Result<List<SmobUser>>
    suspend fun saveSmobUser(smobUser: SmobUser)
    suspend fun getSmobUser(id: String): Result<SmobUser>
    suspend fun deleteAllSmobUsers()
    suspend fun refreshSmobUserDataInDB()
}