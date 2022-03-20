package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupATO
import kotlinx.coroutines.flow.Flow

/**
 * Main entry point for accessing smob group data.
 * ... using domain data types (to abstract details of the underlying persistent storage)
 * ... wrapping results in Resource type (w/h state SUCCESS, ERROR, LOADING)
 */
interface SmobGroupDataSource: SmobItemDataSource<SmobGroupATO> {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    override fun getSmobItem(id: String): Flow<Resource<SmobGroupATO?>>
    override fun getAllSmobItems(): Flow<Resource<List<SmobGroupATO>>>

    // By default Room runs suspend queries off the main thread
    override suspend fun saveSmobItem(smobItemATO: SmobGroupATO)
    override suspend fun saveSmobItems(smobItemsATO: List<SmobGroupATO>)
    override suspend fun updateSmobItem(smobItemATO: SmobGroupATO)
    override suspend fun updateSmobItems(smobItemsATO: List<SmobGroupATO>)
    override suspend fun deleteSmobItem(id: String)
    override suspend fun deleteAllSmobItems()
    override suspend fun refreshDataInLocalDB()

}