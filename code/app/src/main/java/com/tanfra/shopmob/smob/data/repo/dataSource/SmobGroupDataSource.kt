package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.types.SmobGroup

/**
 * Main entry point for accessing smob group data.
 */
interface SmobGroupDataSource {
    suspend fun getSmobGroups(): Resource<List<SmobGroup>>
    suspend fun saveSmobGroup(smobGroup: SmobGroup)
    suspend fun getSmobGroup(id: String): Resource<SmobGroup>
    suspend fun deleteAllSmobGroups()
}