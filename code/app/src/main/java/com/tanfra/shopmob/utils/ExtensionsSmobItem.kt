package com.tanfra.shopmob.utils

import com.tanfra.shopmob.smob.types.SmobItem
import com.tanfra.shopmob.smob.data.local.dto.SmobItemDTO

// extension functions to convert between database types and domain data types (both directions)

// List<SmobItemDTO> --> List<SmobItem>
fun List<SmobItemDTO>.asDomainModel(): List<SmobItem> {
    return map {
        SmobItem (
            id = it.id,
            title = it.title,
            description = it.description,
            location = it.location,
            latitude = it.latitude,
            longitude = it.longitude,
        )
    }
}

// List<SmobItem> --> List<SmobItemDTO>
fun List<SmobItem>.asDatabaseModel(): List<SmobItemDTO> {
    return map {
        SmobItemDTO (
            id = it.id,
            title = it.title,
            description = it.description,
            location = it.location,
            latitude = it.latitude,
            longitude = it.longitude,
        )
    }
}

// SmobItemDTO --> SmobItem
fun SmobItemDTO.asDomainModel(): SmobItem {
    return SmobItem (
        id = this.id,
        title = this.title,
        description = this.description,
        location = this.location,
        latitude = this.latitude,
        longitude = this.longitude,
    )
}

// SmobItem --> SmobItemDTO
fun SmobItem.asDatabaseModel(): SmobItemDTO {
    return SmobItemDTO (
        id = this.id,
        title = this.title,
        description = this.description,
        location = this.location,
        latitude = this.latitude,
        longitude = this.longitude,
    )
}

