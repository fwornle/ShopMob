package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import kotlinx.coroutines.flow.Flow

/**
 * Main entry point for accessing smob list data.
 * ... using domain data types (to abstract details of the underlying persistent storage)
 * ... wrapping results in Resource type (w/h state SUCCESS, ERROR, LOADING)
 */
interface SmobListDataSource: SmobItemDataSource<SmobListATO> {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    override fun getSmobItem(id: String): Flow<Resource<SmobListATO?>>
    override fun getAllSmobItems(): Flow<Resource<List<SmobListATO>>>

    // By default Room runs suspend queries off the main thread
    override suspend fun saveSmobItem(smobItemATO: SmobListATO)
    override suspend fun saveSmobItems(smobItemsATO: List<SmobListATO>)
    override suspend fun updateSmobItem(smobItemATO: SmobListATO)
    override suspend fun updateSmobItems(smobItemsATO: List<SmobListATO>)
    override suspend fun deleteSmobItem(id: String)
    override suspend fun deleteAllSmobItems()
    override suspend fun refreshDataInLocalDB()
    override suspend fun refreshSmobItemInLocalDB(id: String)

    suspend fun refreshSmobListInRemoteDB(smobItemATO: SmobListATO)

}