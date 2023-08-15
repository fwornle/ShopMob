package com.tanfra.shopmob.smob.data.local.dto2ato

import com.tanfra.shopmob.smob.data.local.dto.*
import com.tanfra.shopmob.smob.data.repo.ato.Ato
import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO

// ATO --> DTO
@Suppress("USELESS_CAST", "UNCHECKED_CAST")
fun <DTO: Dto, ATO: Ato> ATO._asDatabaseModel(d: DTO): DTO {

    // note: need "unnecessary cast" to tell Kotlin (and IntelliJ IDEA) that "d is a subclass of
    //       a sealed class"
    return when (d as Dto) {
            is SmobGroupDTO -> {
                SmobGroupDTO(
                    id = (this as SmobGroupATO).id,
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
                id = (this as SmobListATO).id,
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
                id = (this as SmobProductATO).id,
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
                id = (this as SmobShopATO).id,
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
                id = (this as SmobUserATO).id,
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
