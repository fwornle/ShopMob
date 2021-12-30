package com.tanfra.shopmob.smob.data.net.api

import com.tanfra.shopmob.Constants
import com.tanfra.shopmob.smob.data.net.nto.SmobUserNTO
import retrofit2.Response
import retrofit2.http.*

// service interface (exposed by retrofit library) for "SmobUsers" at API endpoint "api/1/users"
// ... using retrofit's built-in coroutine capabilities (>= 2.6.0)
interface SmobUserApi {

        // HTTP GET (fetch a specific user)
        @GET("${Constants.SMOB_API_URL}/users/{id}")
        suspend fun getSmobUserById(
                @Path(value = "id", encoded = true) id: String
                // @Query("q") searchText: String,
        ): Response<SmobUserNTO>

        // HTTP GET (fetch all users)
        @GET("${Constants.SMOB_API_URL}/users")
        suspend fun getSmobUsers(
                // --> @Query annotation can be used to provide additional query parameters
                // @Query("q") searchText: String,
        ): Response<ArrayList<SmobUserNTO>>

        // HTTP POST (insert a new user)
        @POST("${Constants.SMOB_API_URL}/users")
        suspend fun saveSmobUser(
                @Body newUser: SmobUserNTO
        ): Response<Void>

        // HTTP PUT (update a specific user)
        @PUT("${Constants.SMOB_API_URL}/users/{id}")
        suspend fun updateSmobUserById(
                @Path(value = "id", encoded = true) id: String,
                @Body newUser: SmobUserNTO
        ): Response<Void>

        // HTTP DELETE (delete a specific user)
        @DELETE("${Constants.SMOB_API_URL}/users/{id}")
        suspend fun deleteSmobUserById(
                @Path(value = "id", encoded = true) id: String,
        ): Response<Void>

}
