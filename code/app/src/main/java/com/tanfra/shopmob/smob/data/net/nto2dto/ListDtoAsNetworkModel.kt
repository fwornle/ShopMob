package com.tanfra.shopmob.smob.data.net.nto2dto

import com.tanfra.shopmob.smob.data.local.dto.Dto
import com.tanfra.shopmob.smob.data.local.dto.SmobGroupDTO
import com.tanfra.shopmob.smob.data.local.dto.SmobListDTO
import com.tanfra.shopmob.smob.data.local.dto.SmobProductDTO
import com.tanfra.shopmob.smob.data.local.dto.SmobShopDTO
import com.tanfra.shopmob.smob.data.local.dto.SmobUserDTO
import com.tanfra.shopmob.smob.data.local.utils.ActivityStatus
import com.tanfra.shopmob.smob.data.local.utils.InShop
import com.tanfra.shopmob.smob.data.local.utils.ProductCategory
import com.tanfra.shopmob.smob.data.local.utils.ShopLocation
import com.tanfra.shopmob.smob.data.local.utils.SmobListLifecycle
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
                            id = (it as SmobGroupDTO).id,
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
                            id = (it as SmobListDTO).id,
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
                            id = (it as SmobProductDTO).id,
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
                        ) as NTO
                    },
                )
            }
            is SmobUserDTO -> {
                ArrayList(
                    map {
                        SmobUserNTO(
                            id = (it as SmobUserDTO).id,
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
