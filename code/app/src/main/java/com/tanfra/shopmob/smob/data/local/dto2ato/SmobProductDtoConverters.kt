package com.tanfra.shopmob.smob.data.local.dto2ato

import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.local.dto.SmobProductDTO
import com.tanfra.shopmob.smob.data.local.utils.ActivityState
import com.tanfra.shopmob.smob.data.local.utils.ProductCategory

// extension functions to convert between database types and domain data types (both directions)

// List<SmobProductDTO> --> List<SmobProductATO>
fun List<SmobProductDTO>.asDomainModel(): List<SmobProductATO> {
    return map {
        SmobProductATO (
            id = it.id,
            name = it.name,
            description = it.description,
            imageUrl = it.imageUrl,
            category = ProductCategory(it.categoryMain, it.categorySub),
            activity = ActivityState(it.activityDate, it.activityReps),
        )
    }
}

// List<SmobProductATO> --> List<SmobProductDTO>
fun List<SmobProductATO>.asDatabaseModel(): List<SmobProductDTO> {
    return map {
        SmobProductDTO (
            id = it.id,
            name = it.name,
            description = it.description,
            imageUrl = it.imageUrl,
            categoryMain = it.category.main,
            categorySub = it.category.sub,
            activityDate = it.activity.date,
            activityReps = it.activity.reps,
        )
    }
}

// SmobProductDTO --> SmobProductATO
fun SmobProductDTO.asDomainModel(): SmobProductATO {
    return SmobProductATO (
        id = this.id,
        name = this.name,
        description = this.description,
        imageUrl = this.imageUrl,
        category = ProductCategory(this.categoryMain, this.categorySub),
        activity = ActivityState(this.activityDate, this.activityReps),
    )
}

// SmobProductATO --> SmobProductDTO
fun SmobProductATO.asDatabaseModel(): SmobProductDTO {
    return SmobProductDTO (
        id = this.id,
        name = this.name,
        description = this.description,
        imageUrl = this.imageUrl,
        categoryMain = this.category.main,
        categorySub = this.category.sub,
        activityDate = this.activity.date,
        activityReps = this.activity.reps,
    )
}

