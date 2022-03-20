package com.tanfra.shopmob.smob.data.net.api

import com.tanfra.shopmob.smob.data.net.nto.Nto
import retrofit2.Response

/**
 * Generic network service interface for "SmobItems".
 */
interface SmobItemApi<NTO: Nto> {

        suspend fun getSmobItemById(id: String): Response<NTO>
        suspend fun getSmobItems(): Response<ArrayList<NTO>>
        suspend fun saveSmobItem(newItem: NTO?): Response<Void>
        suspend fun updateSmobItemById(id: String, newItem: NTO?): Response<Void>
        suspend fun deleteSmobItemById(id: String): Response<Void>

}
