package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.ato.Ato
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Main entry point for accessing smob group data.
 * ... using domain data types (to abstract details of the underlying persistent storage)
 * ... wrapping results in Resource type (w/h state SUCCESS, ERROR, LOADING)
 */
interface SmobItemDataSource<ATO: Ato> {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    fun getSmobItem(id: String): Flow<Resource<ATO?>>
    fun getAllSmobItems(): Flow<Resource<List<ATO?>>>

    // By default Room runs suspend queries off the main thread
    suspend fun saveSmobItem(smobItemATO: ATO)
    suspend fun saveSmobItems(smobItemsATO: List<ATO>)
    suspend fun updateSmobItem(smobItemATO: ATO)
    suspend fun updateSmobItems(smobItemsATO: List<ATO>)
    suspend fun deleteSmobItem(id: String)
    suspend fun deleteAllSmobItems()
    suspend fun refreshDataInLocalDB()

}