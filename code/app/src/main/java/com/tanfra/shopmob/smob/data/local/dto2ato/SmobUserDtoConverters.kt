package com.tanfra.shopmob.smob.data.local.dto2ato

import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO
import com.tanfra.shopmob.smob.data.local.dto.SmobUserDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

// extension functions to convert between database types and domain data types (both directions)

// Flow<List<SmobUserDTO>> --> Flow<List<SmobUserATO>>
fun Flow<List<SmobUserDTO>>.asDomainModel(): Flow<List<SmobUserATO>> = transform {
    value ->
    emit(
        value.map {
            SmobUserATO (
                id = it.id,
                status = it.status,
                position = it.position,
                username = it.username,
                name = it.name,
                email = it.email,
                imageUrl = it.imageUrl,
                groups = it.groups,
                )
        }
    )
}

// List<SmobUserATO> --> List<SmobUserDTO>
fun List<SmobUserATO>.asDatabaseModel(): List<SmobUserDTO> {
    return map {
            SmobUserDTO (
                id = it.id,
                status = it.status,
                position = it.position,
                username = it.username,
                name = it.name,
                email = it.email,
                imageUrl = it.imageUrl,
                groups = it.groups,
                )
        }
}

// Flow<SmobUserDTO?> --> Flow<SmobUserATO?>
// ... need to add an annotation to avoid a clash at byte-code level (same signature as List<> case)
@JvmName("asDomainModelSmobUserDTO")
fun Flow<SmobUserDTO?>.asDomainModel(): Flow<SmobUserATO?> = transform {
    value ->
    emit(
        value?.let {
            SmobUserATO(
                id = it.id,
                status = it.status,
                position = it.position,
                username = it.username,
                name = it.name,
                email = it.email,
                imageUrl = it.imageUrl,
                groups = it.groups,
                )
        }
    )
}

// SmobUserATO? --> SmobUserDTO?
fun SmobUserATO.asDatabaseModel(): SmobUserDTO {
    return this.let {
            SmobUserDTO(
                id = it.id,
                status = it.status,
                position = it.position,
                username = it.username,
                name = it.name,
                email = it.email,
                imageUrl = it.imageUrl,
                groups = it.groups,
                )
        }
}
