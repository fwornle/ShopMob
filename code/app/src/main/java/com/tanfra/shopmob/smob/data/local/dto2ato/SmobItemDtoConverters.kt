package com.tanfra.shopmob.smob.data.local.dto2ato

import com.tanfra.shopmob.smob.data.repo.ato.SmobItemATO
import com.tanfra.shopmob.smob.data.local.dto.SmobItemDTO

// extension functions to convert between database types and domain data types (both directions)

// List<SmobItemDTO> --> List<SmobItem>
fun List<SmobItemDTO>.asDomainModel(): List<SmobItemATO> {
    return map {
        SmobItemATO (
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
fun List<SmobItemATO>.asDatabaseModel(): List<SmobItemDTO> {
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
fun SmobItemDTO.asDomainModel(): SmobItemATO {
    return SmobItemATO (
        id = this.id,
        title = this.title,
        description = this.description,
        location = this.location,
        latitude = this.latitude,
        longitude = this.longitude,
    )
}

// SmobItem --> SmobItemDTO
fun SmobItemATO.asDatabaseModel(): SmobItemDTO {
    return SmobItemDTO (
        id = this.id,
        title = this.title,
        description = this.description,
        location = this.location,
        latitude = this.latitude,
        longitude = this.longitude,
    )
}

