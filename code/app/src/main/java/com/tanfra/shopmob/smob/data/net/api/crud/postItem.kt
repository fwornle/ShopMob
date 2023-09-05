package com.tanfra.shopmob.smob.data.net.api.crud

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.post


// extension function to hide some Ktor boiler plate (runCatching & body)
suspend inline fun <reified R> HttpClient.postItem(
    urlString: String,
    builder: HttpRequestBuilder.() -> Unit = {}
): Result<R> = runCatching { post(urlString, builder).body() }
