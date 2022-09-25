package com.tanfra.shopmob.smob.data.net.utils


import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.IOException

// ref: adapted from a combination of two articles:
//    https://medium.com/programming-lite/retrofit-2-handling-network-error-defc7d373ad1
//    https://medium.com/@veniamin.vynohradov/monitoring-internet-connection-state-in-android-da7ad915b5e5
//
// interceptor (instantiated as singleton in netServices.kt)
// ... and used there too (in the builder of the okhttp3 client)
// --> implementation of the 'networkConnectionManager' interface is injected via constructor
//     (also instantiated as singleton in netServices.kt)
class NetworkConnectionInterceptor(val networkConnectionManager: NetworkConnectionManager)
    : Interceptor
{

    // callback which hooks our connectivity check into the OkHttpClient as interceptor/middleware
    // (see: netServices.kt)
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        // check connectivity
        if (!networkConnectionManager.isNetworkConnected) {
            // not connected --> throw (our custom) "No Internet Connection" exception
            throw NoConnectivityException()
        }

        // all good (= connected) --> proceed with the next interceptor/middleware
        val builder: Request.Builder = chain.request().newBuilder()
        return chain.proceed(builder.build())

    }

}