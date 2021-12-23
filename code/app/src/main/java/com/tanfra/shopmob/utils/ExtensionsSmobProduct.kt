package com.tanfra.shopmob.utils

import com.tanfra.shopmob.smob.types.SmobProduct
import com.tanfra.shopmob.smob.data.local.dto.SmobProductDTO

// extension functions to convert between database types and domain data types (both directions)

// List<SmobProductDTO> --> List<SmobProduct>
fun List<SmobProductDTO>.asDomainModel(): List<SmobProduct> {
    return map {
        SmobProduct (
            id = it.productId,
            name = it.name,
            description = it.description,
            image = it.image,
            category = ProductCategory(it.categoryMain, it.categorySub),
            activityState = it.activityState,
        )
    }
}

// List<SmobProduct> --> List<SmobProductDTO>
fun List<SmobProduct>.asDatabaseModel(): List<SmobProductDTO> {
    return map {
        SmobProductDTO (
            productId = it.id,
            name = it.name,
            description = it.description,
            image = it.image,
            categoryMain = it.category.main,
            categorySub = it.category.sub,
            activityState = it.activityState,
        )
    }
}

// SmobProductDTO --> SmobProduct
fun SmobProductDTO.asDomainModel(): SmobProduct {
    return SmobProduct (
        id = this.productId,
        name = this.name,
        description = this.description,
        image = this.image,
        category = ProductCategory(this.categoryMain, this.categorySub),
        activityState = this.activityState,
    )
}

// SmobProduct --> SmobProductDTO
fun SmobProduct.asDatabaseModel(): SmobProductDTO {
    return SmobProductDTO (
        productId = this.id,
        name = this.name,
        description = this.description,
        image = this.image,
        categoryMain = this.category.main,
        categorySub = this.category.sub,
        activityState = this.activityState,
    )
}

