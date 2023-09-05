package com.tanfra.shopmob.smob.data.remote.api.crud

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete


// extension function to hide some Ktor boiler plate (runCatching & body)
suspend inline fun <reified R> HttpClient.deleteItem(
    urlString: String,
    builder: HttpRequestBuilder.() -> Unit = {}
): Result<R> = runCatching { delete(urlString, builder).body() }
