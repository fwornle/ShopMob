package com.tanfra.shopmob.smob.data.net.api

import com.tanfra.shopmob.Constants
import com.tanfra.shopmob.smob.data.net.nto.SmobListNTO
import retrofit2.Response
import retrofit2.http.*

/**
 * Service interface (exposed by retrofit library) for "SmobLists".
 */
interface SmobListApi {

        // HTTP GET (fetch a specific list)
        @GET("${Constants.SMOB_API_URL}/lists/{id}")
        suspend fun getSmobListById(
                @Path(value = "id", encoded = true) id: String
                // @Query("q") searchText: String,
        ): Response<SmobListNTO>

        // HTTP GET (fetch all lists)
        @GET("${Constants.SMOB_API_URL}/lists")
        suspend fun getSmobLists(
                // --> @Query annotation can be used to provide additional query parameters
                // @Query("q") searchText: String,
        ): Response<ArrayList<SmobListNTO>>

        // HTTP POST (insert a new list)
        @POST("${Constants.SMOB_API_URL}/lists")
        suspend fun saveSmobList(
                @Body newList: SmobListNTO
        ): Response<Void>

        // HTTP PUT (update a specific list)
        @PUT("${Constants.SMOB_API_URL}/lists/{id}")
        suspend fun updateSmobListById(
                @Path(value = "id", encoded = true) id: String,
                @Body newList: SmobListNTO
        ): Response<Void>

        // HTTP DELETE (delete a specific list)
        @DELETE("${Constants.SMOB_API_URL}/lists/{id}")
        suspend fun deleteSmobListById(
                @Path(value = "id", encoded = true) id: String,
        ): Response<Void>

}
