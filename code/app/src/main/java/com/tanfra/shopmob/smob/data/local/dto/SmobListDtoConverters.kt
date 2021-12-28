package com.tanfra.shopmob.utils

import com.tanfra.shopmob.smob.types.SmobList
import com.tanfra.shopmob.smob.data.local.dto.SmobListDTO

// extension functions to convert between database types and domain data types (both directions)

// List<SmobListDTO> --> List<SmobList>
fun List<SmobListDTO>.asDomainModel(): List<SmobList> {
    return map {
        SmobList (
            id = it.id,
            name = it.name,
            description = it.description,
            products = it.products,
            lifecycle = it.lifecycle,
        )
    }
}

// List<SmobList> --> List<SmobListDTO>
fun List<SmobList>.asDatabaseModel(): List<SmobListDTO> {
    return map {
        SmobListDTO (
            id = it.id,
            name = it.name,
            description = it.description,
            products = it.products,
            lifecycle = it.lifecycle,
        )
    }
}

// SmobListDTO --> SmobList
fun SmobListDTO.asDomainModel(): SmobList {
    return SmobList (
        id = this.id,
        name = this.name,
        description = this.description,
        products = this.products,
        lifecycle = this.lifecycle,
    )
}

// SmobList --> SmobListDTO
fun SmobList.asDatabaseModel(): SmobListDTO {
    return SmobListDTO (
        id = this.id,
        name = this.name,
        description = this.description,
        products = this.products,
        lifecycle = this.lifecycle,
    )
}