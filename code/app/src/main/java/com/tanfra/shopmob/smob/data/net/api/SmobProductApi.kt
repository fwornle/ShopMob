package com.tanfra.shopmob.smob.data.net.api

import com.tanfra.shopmob.Constants
import com.tanfra.shopmob.smob.data.net.nto.SmobProductNTO
import retrofit2.Response
import retrofit2.http.*

/**
 * Service interface (exposed by retrofit library) for "SmobProducts".
 */
interface SmobProductApi {

        // HTTP GET (fetch a specific product)
        @GET("${Constants.SMOB_API_URL}/products/{id}")
        suspend fun getSmobProductById(
                @Path(value = "id", encoded = true) id: String
                // @Query("q") searchText: String,
        ): Response<SmobProductNTO>

        // HTTP GET (fetch all products)
        @GET("${Constants.SMOB_API_URL}/products")
        suspend fun getSmobProducts(
                // --> @Query annotation can be used to provide additional query parameters
                // @Query("q") searchText: String,
        ): Response<ArrayList<SmobProductNTO>>

        // HTTP POST (insert a new product)
        @POST("${Constants.SMOB_API_URL}/products")
        suspend fun saveSmobProduct(
                @Body newProduct: SmobProductNTO
        ): Response<Void>

        // HTTP PUT (update a specific product)
        @PUT("${Constants.SMOB_API_URL}/products/{id}")
        suspend fun updateSmobProductById(
                @Path(value = "id", encoded = true) id: String,
                @Body newProduct: SmobProductNTO
        ): Response<Void>

        // HTTP DELETE (delete a specific product)
        @DELETE("${Constants.SMOB_API_URL}/products/{id}")
        suspend fun deleteSmobProductById(
                @Path(value = "id", encoded = true) id: String,
        ): Response<Void>

}
