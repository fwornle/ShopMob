package com.tanfra.shopmob.smob.data.net.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.put


// extension function to hide some Ktor boiler plate (runCatching & body)
suspend inline fun <reified R> HttpClient.putItem(
    urlString: String,
    builder: HttpRequestBuilder.() -> Unit = {}
): Result<R> = runCatching { put(urlString, builder).body() }