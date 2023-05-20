package com.tanfra.shopmob.smob.data.net.nto2dto

import com.tanfra.shopmob.smob.data.local.dto.Dto
import com.tanfra.shopmob.smob.data.local.dto.SmobGroupDTO
import com.tanfra.shopmob.smob.data.local.dto.SmobListDTO
import com.tanfra.shopmob.smob.data.local.dto.SmobProductDTO
import com.tanfra.shopmob.smob.data.local.dto.SmobShopDTO
import com.tanfra.shopmob.smob.data.local.dto.SmobUserDTO
import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.InShop
import com.tanfra.shopmob.smob.data.types.ProductCategory
import com.tanfra.shopmob.smob.data.types.ShopLocation
import com.tanfra.shopmob.smob.data.types.SmobListLifecycle
import com.tanfra.shopmob.smob.data.net.nto.Nto
import com.tanfra.shopmob.smob.data.net.nto.SmobGroupNTO
import com.tanfra.shopmob.smob.data.net.nto.SmobListNTO
import com.tanfra.shopmob.smob.data.net.nto.SmobProductNTO
import com.tanfra.shopmob.smob.data.net.nto.SmobShopNTO
import com.tanfra.shopmob.smob.data.net.nto.SmobUserNTO

// DTO --> NTO
@Suppress("USELESS_CAST", "UNCHECKED_CAST")
fun <DTO: Dto, NTO: Nto> List<DTO?>._asNetworkModel(d: DTO): ArrayList<NTO> {

    return when (d as Dto) {

            is SmobGroupDTO -> {
                ArrayList(
                    map {
                        SmobGroupNTO(
                            itemId = (it as SmobGroupDTO).itemId,
                            itemStatus = it.itemStatus,
                            itemPosition = it.itemPosition,
                            name = it.name,
                            description = it.description,
                            type = it.type,
                            members = it.members,
                            activity = ActivityStatus(it.activityDate, it.activityReps),
                        ) as NTO
                    },
                )
            }
            is SmobListDTO -> {
                ArrayList(
                    map {
                        SmobListNTO(
                            itemId = (it as SmobListDTO).itemId,
                            itemStatus = it.itemStatus,
                            itemPosition = it.itemPosition,
                            name = it.name,
                            description = it.description,
                            items = it.items,
                            groups = it.groups,
                            lifecycle = SmobListLifecycle(it.lcStatus, it.lcCompletion),
                        ) as NTO
                    },
                )
            }
            is SmobProductDTO -> {
                ArrayList(
                    map {
                        SmobProductNTO(
                            itemId = (it as SmobProductDTO).itemId,
                            itemStatus = it.itemStatus,
                            itemPosition = it.itemPosition,
                            name = it.name,
                            description = it.description,
                            imageUrl = it.imageUrl,
                            category = ProductCategory(it.categoryMain, it.categorySub),
                            activity = ActivityStatus(it.activityDate, it.activityReps),
                            inShop = InShop(it.inShopCategory, it.inShopName, it.inShopLocation),
                        ) as NTO
                    },
                )
            }
            is SmobShopDTO -> {
                ArrayList(
                    map {
                        SmobShopNTO(
                            itemId = (it as SmobShopDTO).itemId,
                            itemStatus = it.itemStatus,
                            itemPosition = it.itemPosition,
                            name = it.name,
                            description = it.description,
                            imageUrl = it.imageUrl,
                            location = ShopLocation(it.locLat, it.locLong),
                            type = it.type,
                            category = it.category,
                            business = it.business,
                        ) as NTO
                    },
                )
            }
            is SmobUserDTO -> {
                ArrayList(
                    map {
                        SmobUserNTO(
                            itemId = (it as SmobUserDTO).itemId,
                            itemStatus = it.itemStatus,
                            itemPosition = it.itemPosition,
                            username = it.username,
                            name = it.name,
                            email = it.email,
                            imageUrl = it.imageUrl,
                            groups = it.groups,
                        ) as NTO
                    },
                )
            }

    }  // when(DTO) ... resolving generic type to concrete type

} // return the mapped List
