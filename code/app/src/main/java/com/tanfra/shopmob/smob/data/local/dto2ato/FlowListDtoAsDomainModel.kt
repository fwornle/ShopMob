package com.tanfra.shopmob.smob.data.local.dto2ato

import com.tanfra.shopmob.smob.data.local.dto.*
import com.tanfra.shopmob.smob.data.local.utils.*
import com.tanfra.shopmob.smob.data.repo.ato.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

// DTO --> ATO
fun <DTO: Dto, ATO: Ato> Flow<List<DTO?>>._asDomainModel(d: DTO): Flow<List<ATO>> {

    return transform {
                    value ->
                emit(
                    value.map {

                        // use reified receiver type information to perform concrete conversion
                        when (d as Dto) {
                            is SmobGroupDTO -> {
                                SmobGroupATO(
                                    id = (it as SmobGroupDTO).id,
                                    itemStatus = it.itemStatus,
                                    itemPosition = it.itemPosition,
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
                                    itemStatus = it.itemStatus,
                                    itemPosition = it.itemPosition,
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
                                    itemStatus = it.itemStatus,
                                    itemPosition = it.itemPosition,
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
                                    itemStatus = it.itemStatus,
                                    itemPosition = it.itemPosition,
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
                                    itemStatus = it.itemStatus,
                                    itemPosition = it.itemPosition,
                                    username = it.username,
                                    name = it.name,
                                    email = it.email,
                                    imageUrl = it.imageUrl,
                                    groups = it.groups,
                                ) as ATO
                            }
                        }  // when(DTO) ... resolving generic type to concrete type
                    }  // map ... List<DTO>

                ) // emit ... flowOf(List<ATO>)

    } // return the flow transform

}
