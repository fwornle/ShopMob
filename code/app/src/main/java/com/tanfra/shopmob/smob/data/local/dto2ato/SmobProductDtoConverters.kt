package com.tanfra.shopmob.smob.data.local.dto2ato

import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.local.dto.SmobProductDTO
import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.InShop
import com.tanfra.shopmob.smob.data.types.ProductCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

// extension functions to convert between database types and domain data types (both directions)

// Flow<List<SmobProductDTO>> --> Flow<List<SmobProductATO>>
fun Flow<List<SmobProductDTO>>.asDomainModel(): Flow<List<SmobProductATO>> = transform {
        value ->
    emit(
        value.map {
            SmobProductATO (
                id = it.id,
                status = it.status,
                position = it.position,
                name = it.name,
                description = it.description,
                imageUrl = it.imageUrl,
                category = ProductCategory(it.categoryMain, it.categorySub),
                activity = ActivityStatus(it.activityDate, it.activityReps),
                inShop = InShop(it.inShopCategory, it.inShopName, it.inShopLocation)
            )
        }
    )
}

// List<SmobProductATO> --> List<SmobProductDTO>
fun List<SmobProductATO>.asDatabaseModel(): List<SmobProductDTO> {
    return map {
        SmobProductDTO (
            id = it.id,
            status = it.status,
            position = it.position,
            name = it.name,
            description = it.description,
            imageUrl = it.imageUrl,
            categoryMain = it.category.main,
            categorySub = it.category.sub,
            activityDate = it.activity.date,
            activityReps = it.activity.reps,
            inShopCategory = it.inShop.category,
            inShopName = it.inShop.name,
            inShopLocation = it.inShop.location,
        )
    }
}

// Flow<SmobProductDTO?> --> Flow<SmobProductATO?>
// ... need to add an annotation to avoid a clash at byte-code level (same signature as List<> case)
@JvmName("asDomainModelSmobProductDTO")
fun Flow<SmobProductDTO?>.asDomainModel(): Flow<SmobProductATO?> = transform {
        value ->
    emit(
        value?.let {
            SmobProductATO(
                id = it.id,
                status = it.status,
                position = it.position,
                name = it.name,
                description = it.description,
                imageUrl = it.imageUrl,
                category = ProductCategory(it.categoryMain, it.categorySub),
                activity = ActivityStatus(it.activityDate, it.activityReps),
                inShop = InShop(it.inShopCategory, it.inShopName, it.inShopLocation)
            )
        }
    )
}

// SmobProductATO? --> SmobProductDTO?
fun SmobProductATO.asDatabaseModel(): SmobProductDTO {
    return this.let {
        SmobProductDTO(
            id = it.id,
            status = it.status,
            position = it.position,
            name = it.name,
            description = it.description,
            imageUrl = it.imageUrl,
            categoryMain = it.category.main,
            categorySub = it.category.sub,
            activityDate = it.activity.date,
            activityReps = it.activity.reps,
            inShopCategory = it.inShop.category,
            inShopName = it.inShop.name,
            inShopLocation = it.inShop.location,
            )
    }
}
