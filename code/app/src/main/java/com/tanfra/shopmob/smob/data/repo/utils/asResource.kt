package com.tanfra.shopmob.smob.data.repo.utils

import com.tanfra.shopmob.smob.data.repo.ato.Ato
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

/**
 * A generic inline function that turns a Flow of nullable Application Transfer Objects (ATO)
 * into a flow of Resource wrapped Ato-elements.
 * 
 * @param <T: Ato>        ATO type of the inline function
 * @param msgOnFailure    Resource.Error message (if flow member is null)
</T> */
fun <T: Ato?> Flow<T?>.asResource(msgOnFailure: String?): Flow<Resource<T>> =
    transform { value ->
        if (value != null) return@transform emit(Resource.Success(value))
        else return@transform emit(Resource.Error(Exception("asResource: Flow is null - $msgOnFailure")))
    }

/**
 * A generic function that turns a Flow of nullable Application Transfer Objects (ATO)
 * into a flow of Resource wrapped Ato-elements.
 *
 * @param <T: Ato>        ATO type of the inline function
 * @param msgOnFailure    Resource.Error message (if flow returns empty list)
</T> */
//
// ... need to add an annotation to avoid a clash at byte-code level (same signature as scalar case)
@JvmName("asResourceOfListOfAto")
fun <T: Ato?> Flow<List<T>>.asResource(msgOnFailure: String?): Flow<Resource<List<T>>> =
    transform { value ->
        if (value.isEmpty()) return@transform emit(Resource.Error(Exception("asResource: Flow is empty list - $msgOnFailure")))
        else return@transform emit(Resource.Success(value))
    }