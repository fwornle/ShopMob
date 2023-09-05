package com.tanfra.shopmob.smob.data.remote.nto2dto

import com.tanfra.shopmob.smob.data.local.dto.SmobShopDTO
import com.tanfra.shopmob.smob.data.remote.nto.SmobShopNTO
import com.tanfra.shopmob.smob.data.types.ShopLocation

// extension functions to convert between database types and domain data types (both directions)

// ArrayList<SmobShopNTO> --> List<SmobShopDTO>
fun ArrayList<SmobShopNTO>.asRepoModel(): List<SmobShopDTO> {
    return this.let {
        it.map {
            SmobShopDTO(
                id = it.id,
                status = it.status,
                position = it.position,
                name = it.name,
                description = it.description,
                imageUrl = it.imageUrl,
                locLat = it.location.latitude,
                locLong = it.location.longitude,
                type = it.type,
                category = it.category,
                business = it.business,
            )
        }
    }
}

// List<SmobShopDTO> --> ArrayList<SmobShopNTO>
fun List<SmobShopDTO>.asNetworkModel(): ArrayList<SmobShopNTO> {
    return ArrayList(
        map {
            SmobShopNTO (
                id = it.id,
                status = it.status,
                position = it.position,
                name = it.name,
                description = it.description,
                imageUrl = it.imageUrl,
                location = ShopLocation(it.locLat, it.locLong),
                type = it.type,
                category = it.category,
                business = it.business,
            )
        }
    )
}

// SmobShopNTO --> SmobShopDTO
fun SmobShopNTO.asRepoModel(): SmobShopDTO {
    return SmobShopDTO (
        id = this.id,
        status = this.status,
        position = this.position,
        name = this.name,
        description = this.description,
        imageUrl = this.imageUrl,
        locLat = this.location.latitude,
        locLong = this.location.longitude,
        type = this.type,
        category = this.category,
        business = this.business,
    )
}

// SmobShopDTO --> SmobShopNTO
fun SmobShopDTO.asNetworkModel(): SmobShopNTO {
    return SmobShopNTO (
        id = this.id,
        status = this.status,
        position = this.position,
        name = this.name,
        description = this.description,
        imageUrl = this.imageUrl,
        location = ShopLocation(this.locLat, this.locLong),
        type = this.type,
        category = this.category,
        business = this.business,
    )
}