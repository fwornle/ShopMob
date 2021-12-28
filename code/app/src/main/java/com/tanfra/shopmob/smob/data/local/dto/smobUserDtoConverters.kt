package com.tanfra.shopmob.smob.data.local.dao

import com.tanfra.shopmob.smob.types.SmobUser
import com.tanfra.shopmob.smob.data.local.dto.SmobUserDTO

// extension functions to convert between database types and domain data types (both directions)

// List<SmobUserDTO> --> List<SmobUser>
fun List<SmobUserDTO>.asDomainModel(): List<SmobUser> {
    return map {
        SmobUser (
            id = it.id,
            username = it.username,
            name = it.name,
            email = it.email,
            imageUrl = it.imageUrl,
        )
    }
}

// List<SmobUser> --> List<SmobUserDTO>
fun List<SmobUser>.asDatabaseModel(): List<SmobUserDTO> {
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

// SmobUserDTO --> SmobUser
fun SmobUserDTO.asDomainModel(): SmobUser {
    return SmobUser (
        id = this.id,
        username = this.username,
        name = this.name,
        email = this.email,
        imageUrl = this.imageUrl,
    )
}

// SmobUser --> SmobUserDTO
fun SmobUser.asDatabaseModel(): SmobUserDTO {
    return SmobUserDTO (
        id = this.id,
        username = this.username,
        name = this.name,
        email = this.email,
        imageUrl = this.imageUrl,
    )
}

