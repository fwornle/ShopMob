package com.tanfra.shopmob.smob.data.local.dto2ato

import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.local.dto.SmobListDTO
import com.tanfra.shopmob.smob.data.local.utils.SmobListLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

// extension functions to convert between database types and domain data types (both directions)

// Flow<List<SmobListDTO>> --> Flow<List<SmobListATO>>
fun Flow<List<SmobListDTO>>.asDomainModel(): Flow<List<SmobListATO>> = transform {
        value ->
    emit(
        value.map {
            SmobListATO (
                itemId = it.itemId,
                itemStatus = it.itemStatus,
                itemPosition = it.itemPosition,
                name = it.name,
                description = it.description,
                items = it.items,
                groups = it.groups,
                lifecycle = SmobListLifecycle(it.lcStatus, it.lcCompletion),
            )
        }
    )
}

// List<SmobListATO> --> List<SmobListDTO>
fun List<SmobListATO>.asDatabaseModel(): List<SmobListDTO> {
    return map {
        SmobListDTO (
            itemId = it.itemId,
            itemStatus = it.itemStatus,
            itemPosition = it.itemPosition,
            name = it.name,
            description = it.description,
            items = it.items,
            groups = it.groups,
            lcStatus = it.lifecycle.status,
            lcCompletion = it.lifecycle.completion,
        )
    }
}

// Flow<SmobListDTO?> --> Flow<SmobListATO?>
// ... need to add an annotation to avoid a clash at byte-code level (same signature as List<> case)
@JvmName("asDomainModelSmobListDTO")
fun Flow<SmobListDTO?>.asDomainModel(): Flow<SmobListATO?> = transform {
        value ->
    emit(
        value?.let {
            SmobListATO(
                itemId = it.itemId,
                itemStatus = it.itemStatus,
                itemPosition = it.itemPosition,
                name = it.name,
                description = it.description,
                items = it.items,
                groups = it.groups,
                lifecycle = SmobListLifecycle(it.lcStatus, it.lcCompletion),
            )
        }
    )
}

// SmobListATO? --> SmobListDTO?
fun SmobListATO.asDatabaseModel(): SmobListDTO {
    return this.let {
        SmobListDTO(
            itemId = it.itemId,
            itemStatus = it.itemStatus,
            itemPosition = it.itemPosition,
            name = it.name,
            description = it.description,
            items = it.items,
            groups = it.groups,
            lcStatus = it.lifecycle.status,
            lcCompletion = it.lifecycle.completion,
        )
    }
}
