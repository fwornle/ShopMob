package com.tanfra.shopmob.utils

import com.tanfra.shopmob.smob.types.SmobShop
import com.tanfra.shopmob.smob.data.local.dto.SmobShopDTO

// extension functions to convert between database types and domain data types (both directions)

// List<SmobShopDTO> --> List<SmobShop>
fun List<SmobShopDTO>.asDomainModel(): List<SmobShop> {
    return map {
        SmobShop (
            id = it.shopId,
            name = it.shopname,
            description = it.description,
            type = it.type,
            category = it.category,
            business = it.businessHours,
        )
    }
}

// List<SmobShop> --> List<SmobShopDTO>
fun List<SmobShop>.asDatabaseModel(): List<SmobShopDTO> {
    return map {
        SmobShopDTO (
            shopId = it.id,
            shopname = it.name,
            description = it.description,
            type = it.type,
            category = it.category,
            businessHours = it.business,
        )
    }
}

// SmobShopDTO --> SmobShop
fun SmobShopDTO.asDomainModel(): SmobShop {
    return SmobShop (
        id = this.shopId,
        name = this.shopname,
        description = this.description,
        type = this.type,
        category = this.category,
        business = this.businessHours,
    )
}

// SmobShop --> SmobShopDTO
fun SmobShop.asDatabaseModel(): SmobShopDTO {
    return SmobShopDTO (
        shopId = this.id,
        shopname = this.name,
        description = this.description,
        type = this.type,
        category = this.category,
        businessHours = this.business,
    )
}