package com.tanfra.shopmob.smob.data.net.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.IOException

// ref: https://medium.com/programming-lite/retrofit-2-handling-network-error-defc7d373ad1
//      https://medium.com/@veniamin.vynohradov/monitoring-internet-connection-state-in-android-da7ad915b5e5
class NetworkConnectionInterceptor(context: Context) : Interceptor {

    private val mContext: Context

    init {
        mContext = context
    }

    private val connectivityManager =
        mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // check network connectivity (by calling our extension function to NetworkCapabilities)
    private val isNetworkConnected: Boolean
        get() = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            .isNetworkCapabilitiesValid()

    // extension function to NetworkCapabilities --> check if we are connected (to the router, ...)
    private fun NetworkCapabilities?.isNetworkCapabilitiesValid(): Boolean = when {
        this == null -> false  // not connected
        hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                (hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_VPN) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) -> true  // connected
        else -> false  // not connected
    }

    // callback which hooks our connectivity check into the OkHttpClient as interceptor/middleware
    // (see: netServices.kt)
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        // check connectivity
        if (!isNetworkConnected) {

            // not connected --> throw our custom error message...
            throw NoConnectivityException()

        }

        // all good (= connected) --> proceed with the next interceptor/middleware
        val builder: Request.Builder = chain.request().newBuilder()
        return chain.proceed(builder.build())

    }

}