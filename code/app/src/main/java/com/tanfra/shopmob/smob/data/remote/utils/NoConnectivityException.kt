package com.tanfra.shopmob.smob.data.remote.utils

import okio.IOException

class NoConnectivityException : IOException() {

    // message to be displayed upon detecting "no internet connection"
    override val message: String
        get() = "No Internet Connection"
}