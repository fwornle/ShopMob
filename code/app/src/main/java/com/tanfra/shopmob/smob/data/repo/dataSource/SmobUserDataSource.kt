package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Main entry point for accessing smob user data.
 * ... using domain data types (to abstract details of the underlying persistent storage)
 * ... wrapping results in Resource type (w/h state SUCCESS, ERROR, LOADING)
 */
interface SmobUserDataSource: SmobItemDataSource<SmobUserATO> {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    override fun getSmobItem(id: String): Flow<Resource<SmobUserATO?>>
    override fun getAllSmobItems(): Flow<Resource<List<SmobUserATO>>>
    fun getSmobMembersByGroupId(id: String): Flow<Resource<List<SmobUserATO>>>

    // By default Room runs suspend queries off the main thread
    override suspend fun saveSmobItem(smobItemATO: SmobUserATO)
    override suspend fun saveSmobItems(smobItemsATO: List<SmobUserATO>)
    override suspend fun updateSmobItem(smobItemATO: SmobUserATO)
    override suspend fun updateSmobItems(smobItemsATO: List<SmobUserATO>)
    override suspend fun deleteSmobItem(id: String)
    override suspend fun deleteAllSmobItems()
    override suspend fun refreshDataInLocalDB()

}