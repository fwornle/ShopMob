package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.ato.SmobItemATO

/**
 * Main entry point for accessing smob item data.
 */
interface SmobItemDataSource {
    suspend fun getSmobItems(): Resource<List<SmobItemATO>>
    suspend fun saveSmobItem(smobItemATO: SmobItemATO)
    suspend fun getSmobItem(id: String): Resource<SmobItemATO>
    suspend fun deleteAllSmobItems()
}