package com.tanfra.shopmob.smob.data.net.api

import com.tanfra.shopmob.Constants
import com.tanfra.shopmob.smob.data.local.dto.SmobUserDTO
import retrofit2.Response
import retrofit2.http.GET

// service interface (exposed by retrofit library) for "SmobUsers" at API endpoint "api/1/users"
// ... using retrofit's built-in coroutine capabilities (>= 2.6.0)
interface ApiSmobUsers {

        // HTTP GET
        @GET("${Constants.SMOB_API_URL}/users")
        suspend fun getSmobUsers(
                // --> @Query annotation can be used to provide additional query parameters
                // @Query("q") searchText: String,
        ): Response<ArrayList<SmobUserDTO>>

}