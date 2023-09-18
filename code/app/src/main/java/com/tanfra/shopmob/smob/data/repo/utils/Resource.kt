package com.tanfra.shopmob.smob.data.repo.utils

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Failure(val exception: Exception) : Resource<Nothing>()
    data object Empty : Resource<Nothing>()
}
