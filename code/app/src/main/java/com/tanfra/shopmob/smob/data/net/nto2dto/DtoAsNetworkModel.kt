package com.tanfra.shopmob.smob.data.net.nto2dto

import com.tanfra.shopmob.smob.data.local.dto.*
import com.tanfra.shopmob.smob.data.local.utils.*
import com.tanfra.shopmob.smob.data.net.nto.*

// DTO --> NTO
@Suppress("USELESS_CAST", "UNCHECKED_CAST")
fun <DTO: Dto, NTO: Nto> DTO?._asNetworkModel(d: DTO): NTO {

    return when (d as Dto) {

            is SmobGroupDTO -> {
                SmobGroupNTO(
                    id = (this as SmobGroupDTO).id,
                    itemStatus = this.itemStatus,
                    itemPosition = this.itemPosition,
                    name = this.name,
                    description = this.description,
                    type = this.type,
                    members = this.members,
                    activity = ActivityStatus(this.activityDate, this.activityReps),
                ) as NTO
            }
            is SmobListDTO -> {
                SmobListNTO(
                    id = (this as SmobListDTO).id,
                    itemStatus = this.itemStatus,
                    itemPosition = this.itemPosition,
                    name = this.name,
                    description = this.description,
                    items = this.items,
                    groups = this.groups,
                    lifecycle = SmobListLifecycle(this.lcStatus, this.lcCompletion),
                ) as NTO
            }
            is SmobProductDTO -> {
                SmobProductNTO (
                    id = (this as SmobProductDTO).id,
                    itemStatus = this.itemStatus,
                    itemPosition = this.itemPosition,
                    name = this.name,
                    description = this.description,
                    imageUrl = this.imageUrl,
                    category = ProductCategory(this.categoryMain, this.categorySub),
                    activity = ActivityStatus(this.activityDate, this.activityReps),
                    inShop = InShop(this.inShopCategory, this.inShopName, this.inShopLocation),
                ) as NTO
            }
            is SmobShopDTO -> {
                SmobShopNTO (
                    id = (this as SmobShopDTO).id,
                    itemStatus = this.itemStatus,
                    itemPosition = this.itemPosition,
                    name = this.name,
                    description = this.description,
                    imageUrl = this.imageUrl,
                    location = ShopLocation(this.locLat, this.locLong),
                    type = this.type,
                    category = this.category,
                    business = this.business,
                ) as NTO
            }
            is SmobUserDTO -> {
                SmobUserNTO (
                    id = (this as SmobUserDTO).id,
                    itemStatus = this.itemStatus,
                    itemPosition = this.itemPosition,
                    username = this.username,
                    name = this.name,
                    email = this.email,
                    imageUrl = this.imageUrl,
                    groups = this.groups,
                ) as NTO
            }

    }  // when(DTO) ... resolving generic type to concrete type

}
