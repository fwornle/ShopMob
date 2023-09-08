package com.tanfra.shopmob.smob.data.repo.utils

import io.ktor.client.plugins.ResponseException
import org.koin.core.component.KoinComponent
import java.net.SocketTimeoutException
import kotlin.Exception

// standardize network error handling
// see: https://medium.com/@harmittaa/retrofit-2-6-0-with-koin-and-coroutines-network-error-handling-a5b98b5e5ca0
//
// code: https://github.com/harmittaa/KoinExample/tree/error-handling/app/src/main/java/com/github/harmittaa/koinexample/networking
enum class ErrorCodes(val code: Int) {
    SocketTimeOut(-1)
}

open class ResponseHandler: KoinComponent {

    fun <T : Any> handleSuccess(data: T): Resource<T> {
        return Resource.Success(data)
    }

    fun <T : Any> handleException(e: Exception): Resource<T> {
        return when (e) {
            is ResponseException /* KTOR */ ->
                Resource.Error(Exception(getErrorMessage(e.response.status.value, e.message)))
            is SocketTimeoutException ->
                Resource.Error(Exception(getErrorMessage(ErrorCodes.SocketTimeOut.code, e.message)))
            else ->
                Resource.Error(Exception(getErrorMessage(Int.MAX_VALUE, e.message)))
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