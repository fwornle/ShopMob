package com.tanfra.shopmob.smob.data.remote.utils

import com.tanfra.shopmob.BuildConfig



import okhttp3.Interceptor
import okhttp3.Response

// AUTH: intercept all outgoing requests and append an API key obtained from an ENV variable
// ... see: https://medium.com/@harmittaa/retrofit-2-6-0-with-koin-and-coroutines-4ff45a4792fc
class AuthInterceptor : Interceptor {

    // fetch API key from build config parameter SMOB_NET_API_KEY, see: build.gradle (:app)
    private val APIKEY = BuildConfig.SMOB_NET_API_KEY

    override fun intercept(chain: Interceptor.Chain): Response {

        var req = chain.request()

        // need ("simple") auth?
        if (!APIKEY.equals("null")) {

            // yes, API_KEY given --> add as parameter (auth)
            val url = req.url.newBuilder()
                .addQueryParameter("APPID", APIKEY)
                .build()

            req = req.newBuilder()
                .url(url).build()

        }

        // return (possibly) manipulated request
        return chain.proceed(req)
    }

}
