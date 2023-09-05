package com.tanfra.shopmob.smob.data.remote.nto2dto

import com.tanfra.shopmob.smob.data.local.dto.SmobUserDTO
import com.tanfra.shopmob.smob.data.remote.nto.SmobUserNTO

// extension functions to convert between database types and domain data types (both directions)

// ArrayList<SmobUserNTO> --> List<SmobUserDTO>
fun ArrayList<SmobUserNTO>.asRepoModel(): List<SmobUserDTO> {
    return this.let {
        it.map {
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
}

// List<SmobUserDTO> --> ArrayList<SmobUserNTO>
fun List<SmobUserDTO>.asNetworkModel(): ArrayList<SmobUserNTO> {
    return ArrayList(
        map {
            SmobUserNTO (
                id = it.id,
                status = it.status,
                position = it.position,
                username = it.username,
                name = it.name,
                email = it.email,
                imageUrl = it.imageUrl,
                groups = it.groups,
            )
        },
    )
}

// SmobUserNTO --> SmobUserDTO
fun SmobUserNTO.asRepoModel(): SmobUserDTO {
    return SmobUserDTO (
        id = this.id,
        status = this.status,
        position = this.position,
        username = this.username,
        name = this.name,
        email = this.email,
        imageUrl = this.imageUrl,
        groups = this.groups,
    )
}

// SmobUserDTO --> SmobUserNTO
fun SmobUserDTO.asNetworkModel(): SmobUserNTO {
    return SmobUserNTO (
        id = this.id,
        status = this.status,
        position = this.position,
        username = this.username,
        name = this.name,
        email = this.email,
        imageUrl = this.imageUrl,
        groups = this.groups,
    )
}

