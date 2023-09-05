package com.tanfra.shopmob.smob.data.net.api.crud

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get


// extension function to hide some Ktor boiler plate (runCatching & body)
suspend inline fun <reified R> HttpClient.getItem(
    urlString: String,
    builder: HttpRequestBuilder.() -> Unit = {}
): Result<R> = runCatching { get(urlString, builder).body() }