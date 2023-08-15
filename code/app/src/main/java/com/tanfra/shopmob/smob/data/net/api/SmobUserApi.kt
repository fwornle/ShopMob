package com.tanfra.shopmob.smob.data.net.api

import com.tanfra.shopmob.smob.data.net.nto.SmobUserNTO
import retrofit2.Response

/**
 * Service interface for "SmobUsers".
 */
interface SmobUserApi {

        // @GET("${Constants.SMOB_API_URL}/users/{id}")
        suspend fun getSmobItemById(id: String): Response<SmobUserNTO>

        // @GET("${Constants.SMOB_API_URL}/users")
        suspend fun getSmobItems(): Response<ArrayList<SmobUserNTO>>

        // @Headers("Content-Type: application/json")
        // @POST("${Constants.SMOB_API_URL}/users")
        suspend fun saveSmobItem(newItem: SmobUserNTO?): Response<Void>

        // @Headers("Content-Type: application/json")
        // @PUT("${Constants.SMOB_API_URL}/users/{id}")
        suspend fun updateSmobItemById(id: String, newItem: SmobUserNTO?): Response<Void>

        // @Headers("Content-Type: application/json")
        // @DELETE("${Constants.SMOB_API_URL}/users/{id}")
        suspend fun deleteSmobItemById(id: String): Response<Void>


//        // HTTP GET (fetch a specific user)
//        @GET("${Constants.SMOB_API_URL}/users/{id}")
//        suspend fun getSmobItemById(
//                @Path(value = "id", encoded = true) id: String
//                // @Query("q") searchText: String,
//        ): Response<SmobUserNTO>
//
//        // HTTP GET (fetch all users)
//        @GET("${Constants.SMOB_API_URL}/users")
//        suspend fun getSmobItems(
//                // --> @Query annotation can be used to provide additional query parameters
//                // @Query("q") searchText: String,
//        ): Response<ArrayList<SmobUserNTO>>
//
//        // HTTP POST (insert a new user)
//        @Headers("Content-Type: application/json")
//        @POST("${Constants.SMOB_API_URL}/users")
//        suspend fun saveSmobItem(
//                @Body newItem: SmobUserNTO?
//        ): Response<Void>
//
//        // HTTP PUT (update a specific user)
//        @Headers("Content-Type: application/json")
//        @PUT("${Constants.SMOB_API_URL}/users/{id}")
//        suspend fun updateSmobItemById(
//                @Path(value = "id", encoded = true) id: String,
//                @Body newItem: SmobUserNTO?
//        ): Response<Void>
//
//        // HTTP DELETE (delete a specific user)
//        @Headers("Content-Type: application/json")
//        @DELETE("${Constants.SMOB_API_URL}/users/{id}")
//        suspend fun deleteSmobItemById(
//                @Path(value = "id", encoded = true) id: String,
//        ): Response<Void>

}
