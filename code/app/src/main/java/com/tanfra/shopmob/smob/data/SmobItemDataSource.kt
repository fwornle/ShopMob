package com.tanfra.shopmob.smob.data

import com.tanfra.shopmob.smob.data.dto.SmobItemDTO
import com.tanfra.shopmob.smob.data.dto.Result

/**
 * Main entry point for accessing smob item data.
 */
interface SmobItemDataSource {
    suspend fun getSmobItems(): Result<List<SmobItemDTO>>
    suspend fun saveSmobItem(smobItem: SmobItemDTO)
    suspend fun getSmobItem(id: String): Result<SmobItemDTO>
    suspend fun deleteAllSmobItems()
}