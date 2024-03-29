package com.tanfra.shopmob.smob.data.local.dto2ato

import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.local.dto.SmobShopDTO
import com.tanfra.shopmob.smob.data.types.ShopLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

// extension functions to convert between database types and domain data types (both directions)

// Flow<List<SmobShopDTO>> --> Flow<List<SmobShopATO>>
fun Flow<List<SmobShopDTO>>.asDomainModel(): Flow<List<SmobShopATO>> = transform {
        value ->
    emit(
        value.map {
            SmobShopATO (
                id = it.id,
                status = it.status,
                position = it.position,
                name = it.name,
                description = it.description,
                imageUrl = it.imageUrl,
                location = ShopLocation(it.locLat, it.locLong),
                type = it.type,
                category = it.category,
                business = it.business,
            )
        }
    )
}

// List<SmobShopATO> --> List<SmobShopDTO>
fun List<SmobShopATO>.asDatabaseModel(): List<SmobShopDTO> {
    return map {
        SmobShopDTO (
            id = it.id,
            status = it.status,
            position = it.position,
            name = it.name,
            description = it.description,
            imageUrl = it.imageUrl,
            locLat = it.location.latitude,
            locLong = it.location.longitude,
            type = it.type,
            category = it.category,
            business = it.business,
        )
    }
}

// Flow<SmobShopDTO?> --> Flow<SmobShopATO?>
// ... need to add an annotation to avoid a clash at byte-code level (same signature as List<> case)
@JvmName("asDomainModelSmobShopDTO")
fun Flow<SmobShopDTO?>.asDomainModel(): Flow<SmobShopATO?> = transform {
        value ->
    emit(
        value?.let {
            SmobShopATO(
                id = it.id,
                status = it.status,
                position = it.position,
                name = it.name,
                description = it.description,
                imageUrl = it.imageUrl,
                location = ShopLocation(it.locLat, it.locLong),
                type = it.type,
                category = it.category,
                business = it.business,
            )
        }
    )
}

// SmobShopATO? --> SmobShopDTO?
fun SmobShopATO.asDatabaseModel(): SmobShopDTO {
    return this.let {
        SmobShopDTO(
            id = it.id,
            status = it.status,
            position = it.position,
            name = it.name,
            description = it.description,
            imageUrl = it.imageUrl,
            locLat = it.location.latitude,
            locLong = it.location.longitude,
            type = it.type,
            category = it.category,
            business = it.business,
        )
    }
}
