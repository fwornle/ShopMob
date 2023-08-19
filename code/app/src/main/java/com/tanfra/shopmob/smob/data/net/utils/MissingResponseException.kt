package com.tanfra.shopmob.smob.data.net.utils

import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse


// custom 2xx response exception (404 - Page not found)
class MissingPageException(response: HttpResponse, cachedResponseText: String) :
    ResponseException(response, cachedResponseText) {
    override val message: String = "Custom server error: ${response.call.request.url}. " +
            "Status: ${response.status}. Text: \"$cachedResponseText\""
}