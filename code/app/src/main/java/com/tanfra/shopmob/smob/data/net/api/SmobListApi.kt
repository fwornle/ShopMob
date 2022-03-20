package com.tanfra.shopmob.smob.data.net.api

import com.tanfra.shopmob.Constants
import com.tanfra.shopmob.smob.data.net.nto.SmobListNTO
import retrofit2.Response
import retrofit2.http.*

/**
 * Service interface (exposed by retrofit library) for "SmobLists".
 */
interface SmobListApi  {

        // HTTP GET (fetch a specific list)
        @GET("${Constants.SMOB_API_URL}/lists/{id}")
        suspend fun getSmobItemById(
                @Path(value = "id", encoded = true) id: String
                // @Query("q") searchText: String,
        ): Response<SmobListNTO>

        // HTTP GET (fetch all lists)
        @GET("${Constants.SMOB_API_URL}/lists")
        suspend fun getSmobItems(
                // --> @Query annotation can be used to provide additional query parameters
                // @Query("q") searchText: String,
        ): Response<ArrayList<SmobListNTO>>

        // HTTP POST (insert a new list)
        @Headers("Content-Type: application/json")
        @POST("${Constants.SMOB_API_URL}/lists")
        suspend fun saveSmobItem(
                @Body newItem: SmobListNTO?
        ): Response<Void>

        // HTTP PUT (update a specific list)
        @Headers("Content-Type: application/json")
        @PUT("${Constants.SMOB_API_URL}/lists/{id}")
        suspend fun updateSmobItemById(
                @Path(value = "id", encoded = true) id: String,
                @Body newItem: SmobListNTO?
        ): Response<Void>

        // HTTP DELETE (delete a specific list)
        @Headers("Content-Type: application/json")
        @DELETE("${Constants.SMOB_API_URL}/lists/{id}")
        suspend fun deleteSmobItemById(
                @Path(value = "id", encoded = true) id: String,
        ): Response<Void>

}
