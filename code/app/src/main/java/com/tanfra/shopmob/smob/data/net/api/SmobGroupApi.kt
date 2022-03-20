package com.tanfra.shopmob.smob.data.net.api

import com.tanfra.shopmob.Constants
import com.tanfra.shopmob.smob.data.net.nto.SmobGroupNTO
import retrofit2.Response
import retrofit2.http.*

/**
 * Service interface (exposed by retrofit library) for "SmobGroups".
 */
interface SmobGroupApi {

        // HTTP GET (fetch a specific group)
        @GET("${Constants.SMOB_API_URL}/groups/{id}")
        suspend fun getSmobItemById(
                @Path(value = "id", encoded = true) id: String
                // @Query("q") searchText: String,
        ): Response<SmobGroupNTO>

        // HTTP GET (fetch all groups)
        @GET("${Constants.SMOB_API_URL}/groups")
        suspend fun getSmobItems(
                // --> @Query annotation can be used to provide additional query parameters
                // @Query("q") searchText: String,
        ): Response<ArrayList<SmobGroupNTO>>

        // HTTP POST (insert a new group)
        @Headers("Content-Type: application/json")
        @POST("${Constants.SMOB_API_URL}/groups")
        suspend fun saveSmobItem(
                @Body newItem: SmobGroupNTO?
        ): Response<Void>

        // HTTP PUT (update a specific group)
        @Headers("Content-Type: application/json")
        @PUT("${Constants.SMOB_API_URL}/groups/{id}")
        suspend fun updateSmobItemById(
                @Path(value = "id", encoded = true) id: String,
                @Body newItem: SmobGroupNTO?
        ): Response<Void>

        // HTTP DELETE (delete a specific group)
        @Headers("Content-Type: application/json")
        @DELETE("${Constants.SMOB_API_URL}/groups/{id}")
        suspend fun deleteSmobItemById(
                @Path(value = "id", encoded = true) id: String,
        ): Response<Void>

}
