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
fun <DTO: Dto, ATO: Ato> List<ATO>._asDatabaseModel(d: DTO): List<DTO> {

    return map {

        when (d as Dto) {
            is SmobGroupDTO -> {
                SmobGroupDTO(
                    itemId = (it as SmobGroupATO).itemId.value,
                    itemStatus = it.itemStatus,
                    itemPosition = it.itemPosition.value,
                    name = it.name,
                    description = it.description,
                    type = it.type,
                    members = it.members,
                    activityDate = it.activity.date,
                    activityReps = it.activity.reps,
                ) as DTO
            }
            is SmobListDTO -> {
                SmobListDTO(
                    itemId = (it as SmobListATO).itemId.value,
                    itemStatus = it.itemStatus,
                    itemPosition = it.itemPosition.value,
                    name = it.name,
                    description = it.description,
                    items = it.items,
                    groups = it.groups,
                    lcStatus = it.lifecycle.status,
                    lcCompletion = it.lifecycle.completion,
                ) as DTO
            }
            is SmobProductDTO -> {
                SmobProductDTO (
                    itemId = (it as SmobProductATO).itemId.value,
                    itemStatus = it.itemStatus,
                    itemPosition = it.itemPosition.value,
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
                ) as DTO
            }
            is SmobShopDTO -> {
                SmobShopDTO (
                    itemId = (it as SmobShopATO).itemId.value,
                    itemStatus = it.itemStatus,
                    itemPosition = it.itemPosition.value,
                    name = it.name,
                    description = it.description,
                    imageUrl = it.imageUrl,
                    locLat = it.location.latitude,
                    locLong = it.location.longitude,
                    type = it.type,
                    category = it.category,
                    business = it.business,
                ) as DTO
            }
            is SmobUserDTO -> {
                SmobUserDTO (
                    itemId = (it as SmobUserATO).itemId.value,
                    itemStatus = it.itemStatus,
                    itemPosition = it.itemPosition.value,
                    username = it.username,
                    name = it.name,
                    email = it.email,
                    imageUrl = it.imageUrl,
                    groups = it.groups,
                ) as DTO
            }

        }  // when(ATO) ... resolving generic type to concrete type

    } // return the transformed list

}
