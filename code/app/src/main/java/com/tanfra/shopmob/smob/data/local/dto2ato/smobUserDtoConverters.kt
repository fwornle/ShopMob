package com.tanfra.shopmob.smob.data.local.dto2ato

import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO
import com.tanfra.shopmob.smob.data.local.dto.SmobUserDTO

// extension functions to convert between database types and domain data types (both directions)

// List<SmobUserDTO> --> List<SmobUserATO>
fun List<SmobUserDTO>.asDomainModel(): List<SmobUserATO> {
    return map {
        SmobUserATO (
            id = it.id,
            username = it.username,
            name = it.name,
            email = it.email,
            imageUrl = it.imageUrl,
        )
    }
}

// List<SmobUserATO> --> List<SmobUserDTO>
fun List<SmobUserATO>.asDatabaseModel(): List<SmobUserDTO> {
    return map {
        SmobUserDTO (
            id = it.id,
            username = it.username,
            name = it.name,
            email = it.email,
            imageUrl = it.imageUrl,
        )
    }
}

// SmobUserDTO --> SmobUserATO
fun SmobUserDTO.asDomainModel(): SmobUserATO {
    return SmobUserATO (
        id = this.id,
        username = this.username,
        name = this.name,
        email = this.email,
        imageUrl = this.imageUrl,
    )
}

// SmobUserATO --> SmobUserDTO
fun SmobUserATO.asDatabaseModel(): SmobUserDTO {
    return SmobUserDTO (
        id = this.id,
        username = this.username,
        name = this.name,
        email = this.email,
        imageUrl = this.imageUrl,
    )
}

