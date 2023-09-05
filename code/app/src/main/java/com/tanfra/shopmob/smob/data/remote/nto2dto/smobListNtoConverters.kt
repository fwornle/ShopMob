package com.tanfra.shopmob.smob.data.remote.nto2dto

import com.tanfra.shopmob.smob.data.local.dto.SmobListDTO
import com.tanfra.shopmob.smob.data.remote.nto.SmobListNTO
import com.tanfra.shopmob.smob.data.types.SmobListLifecycle

// extension functions to convert between database types and domain data types (both directions)

// ArrayList<SmobListNTO> --> List<SmobListDTO>
fun ArrayList<SmobListNTO>.asRepoModel(): List<SmobListDTO> {
    return this.let {
        it.map {
            SmobListDTO(
                id = it.id,
                status = it.status,
                position = it.position,
                name = it.name,
                description = it.description,
                items = it.items,
                groups = it.groups,
                lcStatus = it.lifecycle.status,
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
                status = it.status,
                position = it.position,
                name = it.name,
                description = it.description,
                items = it.items,
                groups = it.groups,
                lifecycle = SmobListLifecycle(it.lcStatus, it.lcCompletion),
            )
        }
    )
}

// SmobListNTO --> SmobListDTO
fun SmobListNTO.asRepoModel(): SmobListDTO {
    return SmobListDTO (
        id = this.id,
        status = this.status,
        position = this.position,
        name = this.name,
        description = this.description,
        items = this.items,
        groups = this.groups,
        lcStatus = this.lifecycle.status,
        lcCompletion = this.lifecycle.completion,
    )
}

// SmobListDTO --> SmobListNTO
fun SmobListDTO.asNetworkModel(): SmobListNTO {
    return SmobListNTO (
        id = this.id,
        status = this.status,
        position = this.position,
        name = this.name,
        description = this.description,
        items = this.items,
        groups = this.groups,
        lifecycle = SmobListLifecycle(this.lcStatus, this.lcCompletion),
    )
}