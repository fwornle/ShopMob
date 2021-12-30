package com.tanfra.shopmob.smob.data.net.nto2dto

import com.tanfra.shopmob.smob.data.local.dto.SmobUserDTO
import com.tanfra.shopmob.smob.data.net.nto.SmobUserNTO

// extension functions to convert between database types and domain data types (both directions)

// ArrayList<SmobUserNTO> --> List<SmobUserDTO>
fun ArrayList<SmobUserNTO>.asRepoModel(): List<SmobUserDTO> {
    return this.let {
        it.map {
            SmobUserDTO(
                id = it.id,
                username = it.username,
                name = it.name,
                email = it.email,
                imageUrl = it.imageUrl,
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
                username = it.username,
                name = it.name,
                email = it.email,
                imageUrl = it.imageUrl,
            )
        },
    )
}

// SmobUserNTO --> SmobUserDTO
fun SmobUserNTO.asRepoModel(): SmobUserDTO {
    return SmobUserDTO (
        id = this.id,
        username = this.username,
        name = this.name,
        email = this.email,
        imageUrl = this.imageUrl,
    )
}

// SmobUserDTO --> SmobUserNTO
fun SmobUserDTO.asNetworkModel(): SmobUserNTO {
    return SmobUserNTO (
        id = this.id,
        username = this.username,
        name = this.name,
        email = this.email,
        imageUrl = this.imageUrl,
    )
}

