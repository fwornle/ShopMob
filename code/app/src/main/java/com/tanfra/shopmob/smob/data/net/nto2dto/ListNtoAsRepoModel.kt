package com.tanfra.shopmob.smob.data.net.nto2dto

import com.tanfra.shopmob.smob.data.local.dto.*
import com.tanfra.shopmob.smob.data.net.nto.*

// NTO --> DTO
fun <DTO: Dto, NTO: Nto> ArrayList<NTO>._asRepoModel(d: DTO): List<DTO> {

    return map {

        when (d as Dto) {
            is SmobGroupDTO -> {
                SmobGroupDTO(
                    id = (it as SmobGroupNTO).id,
                    itemStatus = it.itemStatus,
                    itemPosition = it.itemPosition,
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
                    id = (it as SmobListNTO).id,
                    itemStatus = it.itemStatus,
                    itemPosition = it.itemPosition,
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
                    id = (it as SmobProductNTO).id,
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
                ) as DTO
            }
            is SmobShopDTO -> {
                SmobShopDTO (
                    id = (it as SmobShopNTO).id,
                    itemStatus = it.itemStatus,
                    itemPosition = it.itemPosition,
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
                    id = (it as SmobUserNTO).id,
                    itemStatus = it.itemStatus,
                    itemPosition = it.itemPosition,
                    userIdFed = it.userIdFed,
                    userIdContacts = it.userIdContacts,
                    username = it.username,
                    name = it.name,
                    email = it.email,
                    imageUrl = it.imageUrl,
                    groups = it.groups,
                ) as DTO
            }

        }  // when(NTO) ... resolving generic type to concrete type

    } // return the transformed list

}
