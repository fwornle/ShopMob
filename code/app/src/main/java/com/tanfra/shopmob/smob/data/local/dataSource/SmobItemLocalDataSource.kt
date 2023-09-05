package com.tanfra.shopmob.smob.data.local.dataSource

import com.tanfra.shopmob.smob.data.local.dto.Dto
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object - generic super type of all DAO interfaces, generalizing all tables in the DB.
 */
interface SmobItemLocalDataSource<DTO: Dto> {

    fun getSmobItemById(smobItemId: String): Flow<DTO?>
    fun getSmobItems(): Flow<List<DTO>>

    suspend fun saveSmobItem(smobItem: DTO)
    suspend fun updateSmobItem(smobItem: DTO)
    suspend fun deleteSmobItemById(smobItemId: String)
    suspend fun deleteAllSmobItems()

}