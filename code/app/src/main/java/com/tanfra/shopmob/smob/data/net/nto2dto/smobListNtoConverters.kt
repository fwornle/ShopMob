package com.tanfra.shopmob.smob.data.net.nto2dto

import com.tanfra.shopmob.smob.data.local.dto.SmobListDTO
import com.tanfra.shopmob.smob.data.net.nto.SmobListNTO
import com.tanfra.shopmob.smob.data.local.utils.SmobListLifecycle

// extension functions to convert between database types and domain data types (both directions)

// ArrayList<SmobListNTO> --> List<SmobListDTO>
fun ArrayList<SmobListNTO>.asRepoModel(): List<SmobListDTO> {
    return this.let {
        it.map {
            SmobListDTO(
                id = it.id,
                name = it.name,
                description = it.description,
                items = it.items,
                members = it.members,
                lcState = it.lifecycle.state,
                lcCompletion = it.lifecycle.completion,
            )
        }
    }
}

// List<SmobListDTO> --> ArrayList<SmobListNTO>
fun List<SmobListDTO>.asNetworkModel(): ArrayList<SmobListNTO> {
    return ArrayList(
        map {
            SmobListNTO(
                id = it.id,
                name = it.name,
                description = it.description,
                items = it.items,
                members = it.members,
                lifecycle = SmobListLifecycle(it.lcState, it.lcCompletion),
            )
        }
    )
}

// SmobListNTO --> SmobListDTO
fun SmobListNTO.asRepoModel(): SmobListDTO {
    return SmobListDTO (
        id = this.id,
        name = this.name,
        description = this.description,
        items = this.items,
        members = this.members,
        lcState = this.lifecycle.state,
        lcCompletion = this.lifecycle.completion,
    )
}

// SmobListDTO --> SmobListNTO
fun SmobListDTO.asNetworkModel(): SmobListNTO {
    return SmobListNTO (
        id = this.id,
        name = this.name,
        description = this.description,
        items = this.items,
        members = this.members,
        lifecycle = SmobListLifecycle(this.lcState, this.lcCompletion),
    )
}