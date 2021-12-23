package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.local.dto.SmobGroupDTO
import com.tanfra.shopmob.smob.data.repo.Result
import com.tanfra.shopmob.smob.types.SmobGroup

/**
 * Main entry point for accessing smob group data.
 */
interface SmobGroupDataSource {
    suspend fun getSmobGroups(): Result<List<SmobGroup>>
    suspend fun saveSmobGroup(smobGroup: SmobGroup)
    suspend fun getSmobGroup(id: String): Result<SmobGroup>
    suspend fun deleteAllSmobGroups()
}