package com.tanfra.shopmob.smob.data.net.api

import com.tanfra.shopmob.BuildConfig
import com.tanfra.shopmob.Constants
import com.tanfra.shopmob.smob.data.net.api.crud.deleteItem
import com.tanfra.shopmob.smob.data.net.api.crud.getItem
import com.tanfra.shopmob.smob.data.net.api.crud.postItem
import com.tanfra.shopmob.smob.data.net.api.crud.putItem
import com.tanfra.shopmob.smob.data.net.dataSource.SmobShopRemoteDataSource
import com.tanfra.shopmob.smob.data.net.nto.SmobShopNTO
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType


class SmobShopApiImpl(
    private val ktor: HttpClient,
    tableUrlPart: String
): SmobShopRemoteDataSource {

    // assemble endpoint URL (base)
    private val endpointBase = "${BuildConfig.BASE_URL}/${Constants.SMOB_API_URL}/$tableUrlPart"

    override suspend fun getSmobItemById(id: String): Result<SmobShopNTO> =
        ktor.getItem("$endpointBase/$id") {
            contentType(ContentType.Application.Json)
        }

    override suspend fun getSmobItems(): Result<ArrayList<SmobShopNTO>> =
        ktor.getItem(endpointBase) {
            contentType(ContentType.Application.Json)
        }

    override suspend fun saveSmobItem(newItem: SmobShopNTO): Result<Void> =
        ktor.postItem(endpointBase) {
            contentType(ContentType.Application.Json)
            setBody(newItem)
        }

    override suspend fun updateSmobItemById(id: String, newItem: SmobShopNTO): Result<Void> =
        ktor.putItem("$endpointBase/$id") {
            contentType(ContentType.Application.Json)
            setBody(newItem)
        }

    override suspend fun deleteSmobItemById(id: String): Result<Void> =
        ktor.deleteItem("$endpointBase/$id") {
            contentType(ContentType.Application.Json)
        }

}