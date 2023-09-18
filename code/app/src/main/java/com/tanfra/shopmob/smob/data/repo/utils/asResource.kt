package com.tanfra.shopmob.smob.data.repo.utils

import com.tanfra.shopmob.smob.data.repo.ato.Ato
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

/**
 * A generic inline function that turns a Flow of nullable Application Transfer Objects (ATO)
 * into a flow of Resource wrapped Ato-elements.
 * 
 * @param <T: Ato>        ATO type of the inline function
 * @param errMessage?     error message to be wrapped as exception in Resource.Failure
</T> */
fun <T: Ato?> Flow<T?>.asResource(errMessage: String? = null): Flow<Resource<T>> =
    transform { value ->
        errMessage?.let { return@transform emit(Resource.Failure(Exception(errMessage))) }
        if (value == null) return@transform emit(Resource.Empty)
        else return@transform emit(Resource.Success(value))
    }

/**
 * A generic function that turns a Flow of nullable Application Transfer Objects (ATO)
 * into a flow of Resource wrapped Ato-elements.
 *
 * @param <T: Ato>        ATO type of the inline function
 * @param errMessage?     error message to be wrapped as exception in Resource.Failure
</T> */
//
// ... need to add an annotation to avoid a clash at byte-code level (same signature as scalar case)
@JvmName("asResourceOfListOfAto")
fun <T: Ato?> Flow<List<T>>.asResource(errMessage: String? = null): Flow<Resource<List<T>>> =
    transform { value ->
        errMessage?.let { return@transform emit(Resource.Failure(Exception(errMessage))) }
        if (value.isEmpty()) return@transform emit(Resource.Empty)
        else return@transform emit(Resource.Success(value))
    }