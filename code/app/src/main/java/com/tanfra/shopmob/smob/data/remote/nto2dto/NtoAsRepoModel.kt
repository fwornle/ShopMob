package com.tanfra.shopmob.smob.data.remote.nto2dto

import com.tanfra.shopmob.smob.data.local.dto.*
import com.tanfra.shopmob.smob.data.remote.nto.*


// NTO --> DTO
@Suppress("USELESS_CAST", "UNCHECKED_CAST")
fun <DTO: Dto, NTO: Nto> NTO._asRepoModel(d: DTO): DTO {

    return when (d as Dto) {
            is SmobGroupDTO -> {
                SmobGroupDTO(
                    id = (this as SmobGroupNTO).id,
                    status = this.status,
                    position = this.position,
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
                id = (this as SmobListNTO).id,
                status = this.status,
                position = this.position,
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
                id = (this as SmobProductNTO).id,
                status = this.status,
                position = this.position,
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
                id = (this as SmobShopNTO).id,
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
            ) as DTO
        }
        is SmobUserDTO -> {
            SmobUserDTO(
                id = (this as SmobUserNTO).id,
                status = this.status,
                position = this.position,
                username = this.username,
                name = this.name,
                email = this.email,
                imageUrl = this.imageUrl,
                groups = this.groups,
            ) as DTO
        }

    }  // when(DTO) ... resolving generic type to concrete type

}
