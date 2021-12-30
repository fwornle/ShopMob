package com.tanfra.shopmob.smob.data.net.api

import com.tanfra.shopmob.Constants
import com.tanfra.shopmob.smob.data.net.nto.SmobShopNTO
import retrofit2.Response
import retrofit2.http.*

/**
 * Service interface (exposed by retrofit library) for "SmobShops".
 */
interface SmobShopApi {

        // HTTP GET (fetch a specific shop)
        @GET("${Constants.SMOB_API_URL}/shops/{id}")
        suspend fun getSmobShopById(
                @Path(value = "id", encoded = true) id: String
                // @Query("q") searchText: String,
        ): Response<SmobShopNTO>

        // HTTP GET (fetch all shops)
        @GET("${Constants.SMOB_API_URL}/shops")
        suspend fun getSmobShops(
                // --> @Query annotation can be used to provide additional query parameters
                // @Query("q") searchText: String,
        ): Response<ArrayList<SmobShopNTO>>

        // HTTP POST (insert a new shop)
        @POST("${Constants.SMOB_API_URL}/shops")
        suspend fun saveSmobShop(
                @Body newShop: SmobShopNTO
        ): Response<Void>

        // HTTP PUT (update a specific shop)
        @PUT("${Constants.SMOB_API_URL}/shops/{id}")
        suspend fun updateSmobShopById(
                @Path(value = "id", encoded = true) id: String,
                @Body newShop: SmobShopNTO
        ): Response<Void>

        // HTTP DELETE (delete a specific shop)
        @DELETE("${Constants.SMOB_API_URL}/shops/{id}")
        suspend fun deleteSmobShopById(
                @Path(value = "id", encoded = true) id: String,
        ): Response<Void>

}
