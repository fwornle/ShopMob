package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupATO

/**
 * Main entry point for accessing smob group data.
 * ... using domain data types (to abstract details of the underlying persistent storage)
 * ... wrapping results in Resource type (w/h state SUCCESS, ERROR, LOADING)
 */
interface SmobGroupDataSource {
    suspend fun getSmobGroup(id: String): Resource<SmobGroupATO>
    suspend fun getAllSmobGroups(): Resource<List<SmobGroupATO>>
    suspend fun saveSmobGroup(smobGroupATO: SmobGroupATO)
    suspend fun saveSmobGroups(smobGroupsATO: List<SmobGroupATO>)
    suspend fun updateSmobGroup(smobGroupATO: SmobGroupATO)
    suspend fun updateSmobGroups(smobGroupsATO: List<SmobGroupATO>)
    suspend fun deleteSmobGroup(id: String)
    suspend fun deleteAllSmobGroups()
    suspend fun refreshDataInLocalDB()
}