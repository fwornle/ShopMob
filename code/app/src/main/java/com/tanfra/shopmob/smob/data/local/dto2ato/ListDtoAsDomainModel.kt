package com.tanfra.shopmob.smob.data.local.dto2ato

import com.tanfra.shopmob.smob.data.local.dto.*
import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.InShop
import com.tanfra.shopmob.smob.data.types.ProductCategory
import com.tanfra.shopmob.smob.data.types.ShopLocation
import com.tanfra.shopmob.smob.data.types.SmobListLifecycle
import com.tanfra.shopmob.smob.data.repo.ato.*
import com.tanfra.shopmob.smob.data.types.SmobItemId
import com.tanfra.shopmob.smob.data.types.SmobItemPosition

// DTO --> ATO
@Suppress("USELESS_CAST", "UNCHECKED_CAST")
fun <DTO: Dto, ATO: Ato> List<DTO?>._asDomainModel(d: DTO): List<ATO> {

    return map {

        // use reified receiver type information to perform concrete conversion
        when (d as Dto) {
            is SmobGroupDTO -> {
                SmobGroupATO(
                    itemId = SmobItemId((it as SmobGroupDTO).itemId),
                    itemStatus = it.itemStatus,
                    itemPosition = SmobItemPosition(it.itemPosition),
                    name = it.name,
                    description = it.description,
                    type = it.type,
                    members = it.members,
                    activity = ActivityStatus(it.activityDate, it.activityReps),
                ) as ATO
            }
            is SmobListDTO -> {
                SmobListATO(
                    itemId = SmobItemId((it as SmobListDTO).itemId),
                    itemStatus = it.itemStatus,
                    itemPosition = SmobItemPosition(it.itemPosition),
                    name = it.name,
                    description = it.description,
                    items = it.items,
                    groups = it.groups,
                    lifecycle = SmobListLifecycle(it.lcStatus, it.lcCompletion),
                ) as ATO
            }
            is SmobProductDTO -> {
                SmobProductATO (
                    itemId = SmobItemId((it as SmobProductDTO).itemId),
                    itemStatus = it.itemStatus,
                    itemPosition = SmobItemPosition(it.itemPosition),
                    name = it.name,
                    description = it.description,
                    imageUrl = it.imageUrl,
                    category = ProductCategory(it.categoryMain, it.categorySub),
                    activity = ActivityStatus(it.activityDate, it.activityReps),
                    inShop = InShop(it.inShopCategory, it.inShopName, it.inShopLocation),
                ) as ATO
            }
            is SmobShopDTO -> {
                SmobShopATO (
                    itemId = SmobItemId((it as SmobShopDTO).itemId),
                    itemStatus = it.itemStatus,
                    itemPosition = SmobItemPosition(it.itemPosition),
                    name = it.name,
                    description = it.description,
                    imageUrl = it.imageUrl,
                    location = ShopLocation(it.locLat, it.locLong),
                    type = it.type,
                    category = it.category,
                    business = it.business,
                ) as ATO
            }
            is SmobUserDTO -> {
                SmobUserATO (
                    itemId = SmobItemId((it as SmobUserDTO).itemId),
                    itemStatus = it.itemStatus,
                    itemPosition = SmobItemPosition(it.itemPosition),
                    username = it.username,
                    name = it.name,
                    email = it.email,
                    imageUrl = it.imageUrl,
                    groups = it.groups,
                    ) as ATO
            }

        }  // when(DTO) ... resolving generic type to concrete type

    } // return the mapped List

}
