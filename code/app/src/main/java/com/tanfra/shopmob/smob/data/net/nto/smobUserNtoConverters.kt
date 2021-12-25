package com.tanfra.shopmob.smob.data.net.api

import com.tanfra.shopmob.smob.data.local.dto.SmobUserDTO
import com.tanfra.shopmob.smob.data.net.nto.SmobUserNTO

// extension functions to convert between database types and domain data types (both directions)

// ArrayList<SmobUserNTO> --> List<SmobUserDTO>
fun ArrayList<SmobUserNTO>.asRepoModel(): List<SmobUserDTO> {
    return map {
        SmobUserDTO (
            userId = it.userId,
            username = it.username,
            imageUrl = it.imageUrl,
            shops = it.shops,
            groups = it.groups,
            lists = it.lists,
        )
    }
}


/*

// List<SmobUserDTO> --> ArrayList<SmobUserNTO>
fun List<SmobUserDTO>.asNetworkModel(): ArrayList<SmobUserNTO> {
    return map {
        SmobUserNTO (
            userId = it.userId,
            username = it.username,
            imageUrl = it.imageUrl,
            shops = it.shops,
            groups = it.groups,
            lists = it.lists,
        )
    }}

*/

// SmobUserNTO --> SmobUserDTO
fun SmobUserNTO.asRepoModel(): SmobUserDTO {
    return SmobUserDTO (
        userId = this.userId,
        username = this.username,
        imageUrl = this.imageUrl,
        shops = this.shops,
        groups = this.groups,
        lists = this.lists,
    )
}

// SmobUserDTO --> SmobUserNTO
fun SmobUserDTO.asNetworkModel(): SmobUserNTO {
    return SmobUserNTO (
        userId = this.userId,
        username = this.username,
        imageUrl = this.imageUrl,
        shops = this.shops,
        groups = this.groups,
        lists = this.lists,
    )
}

