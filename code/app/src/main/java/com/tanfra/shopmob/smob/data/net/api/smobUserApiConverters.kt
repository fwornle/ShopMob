package com.tanfra.shopmob.smob.data.net.api

import com.tanfra.shopmob.smob.types.SmobUser
import com.tanfra.shopmob.smob.data.local.dto.SmobUserDTO

// extension functions to convert between database types and domain data types (both directions)

// ArrayList<SmobUserDTO> --> List<SmobUser>
fun ArrayList<SmobUserDTO>.asDomainModel(): List<SmobUser> {
    return map {
        SmobUser (
            id = it.userId,
            name = it.username,
            imageUrl = it.imageUrl,
            shops = it.shops,
            groups = it.groups,
            lists = it.lists,
        )
    }
}

// List<SmobUser> --> ArrayList<SmobUserDTO>
fun List<SmobUser>.asNetworkModel(): ArrayList<SmobUserDTO> {
    return map {
        SmobUserDTO (
            userId = it.id,
            username = it.name,
            imageUrl = it.imageUrl,
            shops = it.shops,
            groups = it.groups,
            lists = it.lists,
        )
    }
}

// SmobUserDTO --> SmobUser
fun SmobUserDTO.asDomainModel(): SmobUser {
    return SmobUser (
        id = this.userId,
        name = this.username,
        imageUrl = this.imageUrl,
        shops = this.shops,
        groups = this.groups,
        lists = this.lists,
    )
}

// SmobUser --> SmobUserDTO
fun SmobUser.asNetworkModel(): SmobUserDTO {
    return SmobUserDTO (
        userId = this.id,
        username = this.name,
        imageUrl = this.imageUrl,
        shops = this.shops,
        groups = this.groups,
        lists = this.lists,
    )
}

