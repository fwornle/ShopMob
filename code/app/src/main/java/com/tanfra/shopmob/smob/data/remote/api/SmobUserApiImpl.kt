package com.tanfra.shopmob.smob.data.remote.api

import com.tanfra.shopmob.BuildConfig
import com.tanfra.shopmob.app.Constants
import com.tanfra.shopmob.smob.data.remote.api.crud.deleteItem
import com.tanfra.shopmob.smob.data.remote.api.crud.getItem
import com.tanfra.shopmob.smob.data.remote.api.crud.postItem
import com.tanfra.shopmob.smob.data.remote.api.crud.putItem
import com.tanfra.shopmob.smob.data.remote.dataSource.SmobUserRemoteDataSource
import com.tanfra.shopmob.smob.data.remote.nto.SmobUserNTO
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType


class SmobUserApiImpl(
    private val ktor: HttpClient,
    tableUrlPart: String
): SmobUserRemoteDataSource {

    // assemble endpoint URL (base)
    private val endpointBase = "${BuildConfig.BASE_URL}/${Constants.SMOB_API_URL}/$tableUrlPart"

    override suspend fun getSmobItemById(id: String): Result<SmobUserNTO> =
        ktor.getItem("$endpointBase/$id") {
            contentType(ContentType.Application.Json)
        }

    override suspend fun getSmobItems(): Result<ArrayList<SmobUserNTO>> =
        ktor.getItem(endpointBase) {
            contentType(ContentType.Application.Json)
        }

    override suspend fun saveSmobItem(newItem: SmobUserNTO): Result<Void> =
        ktor.postItem(endpointBase) {
            contentType(ContentType.Application.Json)
            setBody(newItem)
        }

    override suspend fun updateSmobItemById(id: String, newItem: SmobUserNTO): Result<Void> =
        ktor.putItem("$endpointBase/$id") {
            contentType(ContentType.Application.Json)
            setBody(newItem)
        }

    override suspend fun deleteSmobItemById(id: String): Result<Void> =
        ktor.deleteItem("$endpointBase/$id") {
            contentType(ContentType.Application.Json)
        }

}