package com.tanfra.shopmob.smob.data.net.nto2dto

import com.tanfra.shopmob.smob.data.local.dto.*
import com.tanfra.shopmob.smob.data.net.nto.*


// NTO --> DTO
@Suppress("USELESS_CAST", "UNCHECKED_CAST")
fun <DTO: Dto, NTO: Nto> NTO._asRepoModel(d: DTO): DTO {

    return when (d as Dto) {
            is SmobGroupDTO -> {
                SmobGroupDTO(
                    itemId = (this as SmobGroupNTO).itemId,
                    itemStatus = this.itemStatus,
                    itemPosition = this.itemPosition,
                    name = this.name,
                    description = this.description,
                    type = this.type,
                    members = this.members,
                    activityDate = this.activity.date,
                    activityReps = this.activity.reps,
                ) as DTO
            }
        is SmobListDTO -> {
            SmobListDTO(
                itemId = (this as SmobListNTO).itemId,
                itemStatus = this.itemStatus,
                itemPosition = this.itemPosition,
                name = this.name,
                description = this.description,
                items = this.items,
                groups = this.groups,
                lcStatus = this.lifecycle.status,
                lcCompletion = this.lifecycle.completion,
            ) as DTO
        }
        is SmobProductDTO -> {
            SmobProductDTO(
                itemId = (this as SmobProductNTO).itemId,
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
            ) as DTO
        }
        is SmobShopDTO -> {
            SmobShopDTO(
                itemId = (this as SmobShopNTO).itemId,
                itemStatus = this.itemStatus,
                itemPosition = this.itemPosition,
                name = this.name,
                description = this.description,
                imageUrl = this.imageUrl,
                locLat = this.location.latitude,
                locLong = this.location.longitude,
                type = this.type,
                category = this.category,
                business = this.business,
            ) as DTO
        }
        is SmobUserDTO -> {
            SmobUserDTO(
                itemId = (this as SmobUserNTO).itemId,
                itemStatus = this.itemStatus,
                itemPosition = this.itemPosition,
                username = this.username,
                name = this.name,
                email = this.email,
                imageUrl = this.imageUrl,
                groups = this.groups,
            ) as DTO
        }

    }  // when(DTO) ... resolving generic type to concrete type

}
