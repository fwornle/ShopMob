package com.tanfra.shopmob.smob.data.net.nto2dto

import com.tanfra.shopmob.smob.data.local.dto.SmobProductDTO
import com.tanfra.shopmob.smob.data.local.utils.ActivityStatus
import com.tanfra.shopmob.smob.data.local.utils.InShop
import com.tanfra.shopmob.smob.data.local.utils.ProductCategory
import com.tanfra.shopmob.smob.data.net.nto.SmobProductNTO

// extension functions to convert between database types and domain data types (both directions)

// ArrayList<SmobProductNTO> --> List<SmobProductDTO>
fun ArrayList<SmobProductNTO>.asRepoModel(): List<SmobProductDTO> {
    return this.let {
        it.map {
            SmobProductDTO(
                id = it.id,
                itemStatus = it.itemStatus,
                itemPosition = it.itemPosition,
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
}

// List<SmobProductDTO> --> ArrayList<SmobProductNTO>
fun List<SmobProductDTO>.asNetworkModel(): ArrayList<SmobProductNTO> {
    return ArrayList(
        map {
            SmobProductNTO (
                id = it.id,
                itemStatus = it.itemStatus,
                itemPosition = it.itemPosition,
                name = it.name,
                description = it.description,
                imageUrl = it.imageUrl,
                category = ProductCategory(it.categoryMain, it.categorySub),
                activity = ActivityStatus(it.activityDate, it.activityReps),
                inShop = InShop(it.inShopCategory, it.inShopName, it.inShopLocation),
            )
        }
    )
}

// SmobProductNTO --> SmobProductDTO
fun SmobProductNTO.asRepoModel(): SmobProductDTO {
    return SmobProductDTO (
        id = this.id,
        itemStatus = this.itemStatus,
        itemPosition = this.itemPosition,
        name = this.name,
        description = this.description,
        imageUrl = this.imageUrl,
        categoryMain = this.category.main,
        categorySub = this.category.sub,
        activityDate = this.activity.date,
        activityReps = this.activity.reps,
        inShopCategory = this.inShop.category,
        inShopName = this.inShop.name,
        inShopLocation = this.inShop.location,
    )
}

// SmobProductDTO --> SmobProductNTO
fun SmobProductDTO.asNetworkModel(): SmobProductNTO {
    return SmobProductNTO (
        id = this.id,
        itemStatus = this.itemStatus,
        itemPosition = this.itemPosition,
        name = this.name,
        description = this.description,
        imageUrl = this.imageUrl,
        category = ProductCategory(this.categoryMain, this.categorySub),
        activity = ActivityStatus(this.activityDate, this.activityReps),
        inShop = InShop(this.inShopCategory, this.inShopName, this.inShopLocation),
    )
}