package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.Result
import com.tanfra.shopmob.smob.types.SmobItem

/**
 * Main entry point for accessing smob item data.
 */
interface SmobItemDataSource {
    suspend fun getSmobItems(): Result<List<SmobItem>>
    suspend fun saveSmobItem(smobItem: SmobItem)
    suspend fun getSmobItem(id: String): Result<SmobItem>
    suspend fun deleteAllSmobItems()
}