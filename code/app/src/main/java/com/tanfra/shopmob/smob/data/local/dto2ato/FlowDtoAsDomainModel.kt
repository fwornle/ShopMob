package com.tanfra.shopmob.smob.data.local.dto2ato

import com.tanfra.shopmob.smob.data.local.dto.*
import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.InShop
import com.tanfra.shopmob.smob.data.types.ProductCategory
import com.tanfra.shopmob.smob.data.types.ShopLocation
import com.tanfra.shopmob.smob.data.types.SmobListLifecycle
import com.tanfra.shopmob.smob.data.repo.ato.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DTO --> ATO
@Suppress("USELESS_CAST", "UNCHECKED_CAST")
fun <DTO: Dto, ATO: Ato> Flow<DTO?>._asDomainModel(d: DTO): Flow<ATO> {

    return map {

            // use reified receiver type information to perform concrete conversion
            when (d as Dto) {
                is SmobGroupDTO -> {
                    SmobGroupATO(
                        id = (it as SmobGroupDTO).id,
                        status = it.status,
                        position = it.position,
                        name = it.name,
                        description = it.description,
                        type = it.type,
                        members = it.members,
                        activity = ActivityStatus(it.activityDate, it.activityReps),
                    ) as ATO
                }
                is SmobListDTO -> {
                    SmobListATO(
                        id = (it as SmobListDTO).id,
                        status = it.status,
                        position = it.position,
                        name = it.name,
                        description = it.description,
                        items = it.items,
                        groups = it.groups,
                        lifecycle = SmobListLifecycle(it.lcStatus, it.lcCompletion),
                    ) as ATO
                }
                is SmobProductDTO -> {
                    SmobProductATO (
                        id = (it as SmobProductDTO).id,
                        status = it.status,
                        position = it.position,
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
                        id = (it as SmobShopDTO).id,
                        status = it.status,
                        position = it.position,
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
                        id = (it as SmobUserDTO).id,
                        status = it.status,
                        position = it.position,
                        username = it.username,
                        name = it.name,
                        email = it.email,
                        imageUrl = it.imageUrl,
                        groups = it.groups,
                    ) as ATO
                }

            }  // when(DTO) ... resolving generic type to concrete type

    } // return the transformed flow

}
