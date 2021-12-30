package com.tanfra.shopmob.smob.data.local.dto2ato

import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.local.dto.SmobShopDTO
import com.tanfra.shopmob.smob.data.local.utils.ShopLocation

// extension functions to convert between database types and domain data types (both directions)

// List<SmobShopDTO> --> List<SmobShopATO>
fun List<SmobShopDTO>.asDomainModel(): List<SmobShopATO> {
    return map {
        SmobShopATO (
            id = it.id,
            name = it.name,
            description = it.description,
            location = ShopLocation(it.locLat, it.locLat),
            type = it.type,
            category = it.category,
            business = it.business,
        )
    }
}

// List<SmobShopATO> --> List<SmobShopDTO>
fun List<SmobShopATO>.asDatabaseModel(): List<SmobShopDTO> {
    return map {
        SmobShopDTO (
            id = it.id,
            name = it.name,
            description = it.description,
            locLat = it.location.latitude,
            locLong = it.location.longitude,
            type = it.type,
            category = it.category,
            business = it.business,
        )
    }
}

// SmobShopDTO --> SmobShopATO
fun SmobShopDTO.asDomainModel(): SmobShopATO {
    return SmobShopATO (
        id = this.id,
        name = this.name,
        description = this.description,
        location = ShopLocation(this.locLat, this.locLat),
        type = this.type,
        category = this.category,
        business = this.business,
    )
}

// SmobShopATO --> SmobShopDTO
fun SmobShopATO.asDatabaseModel(): SmobShopDTO {
    return SmobShopDTO (
        id = this.id,
        name = this.name,
        description = this.description,
        locLat = this.location.latitude,
        locLong = this.location.longitude,
        type = this.type,
        category = this.category,
        business = this.business,
    )
}