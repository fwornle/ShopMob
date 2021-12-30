package com.tanfra.shopmob.smob.data.local.dto2ato

import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.local.dto.SmobListDTO
import com.tanfra.shopmob.smob.data.local.utils.SmobListLifecycle

// extension functions to convert between database types and domain data types (both directions)

// List<SmobListDTO> --> List<SmobList>
fun List<SmobListDTO>.asDomainModel(): List<SmobListATO> {
    return map {
        SmobListATO (
            id = it.id,
            name = it.name,
            description = it.description,
            items = it.items,
            members = it.members,
            lifecycle = SmobListLifecycle(it.lcState, it.lcCompletion),
        )
    }
}

// List<SmobList> --> List<SmobListDTO>
fun List<SmobListATO>.asDatabaseModel(): List<SmobListDTO> {
    return map {
        SmobListDTO (
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

// SmobListDTO --> SmobList
fun SmobListDTO.asDomainModel(): SmobListATO {
    return SmobListATO (
        id = this.id,
        name = this.name,
        description = this.description,
        items = this.items,
        members = this.members,
        lifecycle = SmobListLifecycle(this.lcState, this.lcCompletion),
    )
}

// SmobList --> SmobListDTO
fun SmobListATO.asDatabaseModel(): SmobListDTO {
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