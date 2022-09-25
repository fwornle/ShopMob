package com.tanfra.shopmob.smob.data.net

import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.work.SmobAppWork
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.HttpException
import timber.log.Timber
import java.lang.Exception
import java.net.SocketTimeoutException

// standardize network error handling
// see: https://medium.com/@harmittaa/retrofit-2-6-0-with-koin-and-coroutines-network-error-handling-a5b98b5e5ca0
//
// code: https://github.com/harmittaa/KoinExample/tree/error-handling/app/src/main/java/com/github/harmittaa/koinexample/networking
enum class ErrorCodes(val code: Int) {
    SocketTimeOut(-1)
}

open class ResponseHandler: KoinComponent {

    // fetch worker class form service locator
    private val wManager: SmobAppWork by inject()

    fun <T : Any> handleSuccess(data: T): Resource<T> {

//        // re-activate network services
//        if (!wManager.netActive) {
//            Timber.i("Successful net read --> re-activating network.")
//            wManager.netActive = true
//        }

        return Resource.success(data)
    }

    fun <T : Any> handleException(e: Exception): Resource<T> {
        return when (e) {
            is HttpException -> Resource.error(getErrorMessage(e.code(), e.message), null)
            is SocketTimeoutException -> {

//                // deactivate network services
//                if (wManager.netActive) {
//                    Timber.i("Timeout --> (temporarily) deactivating network.")
//                    wManager.netActive = false
//                }

                Resource.error(getErrorMessage(ErrorCodes.SocketTimeOut.code, e.message), null)
            }
            else -> Resource.error(getErrorMessage(Int.MAX_VALUE, e.message), null)
        }
    }

    private fun getErrorMessage(code: Int, expMsg: String?): String {
        return when (code) {
            ErrorCodes.SocketTimeOut.code -> "Timeout ($expMsg)"
            401 -> "Unauthorised ($expMsg)"
            404 -> "Not found ($expMsg)"
            else -> "Something went wrong ($expMsg)"
        }
    }
}