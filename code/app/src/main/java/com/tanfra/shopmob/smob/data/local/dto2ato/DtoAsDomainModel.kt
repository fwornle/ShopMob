package com.tanfra.shopmob.smob.data.local.dto2ato

import com.tanfra.shopmob.smob.data.local.dto.*
import com.tanfra.shopmob.smob.data.local.utils.*
import com.tanfra.shopmob.smob.data.repo.ato.*


// DTO --> ATO
@Suppress("USELESS_CAST", "UNCHECKED_CAST")
fun <DTO: Dto, ATO: Ato> DTO?._asDomainModel(d: DTO): ATO {

    return when (d as Dto) {

            is SmobGroupDTO -> {
                SmobGroupATO(
                    id = (this as SmobGroupDTO).id,
                    itemStatus = this.itemStatus,
                    itemPosition = this.itemPosition,
                    name = this.name,
                    description = this.description,
                    type = this.type,
                    members = this.members,
                    activity = ActivityStatus(this.activityDate, this.activityReps),
                ) as ATO
            }
            is SmobListDTO -> {
                SmobListATO(
                    id = (this as SmobListDTO).id,
                    itemStatus = this.itemStatus,
                    itemPosition = this.itemPosition,
                    name = this.name,
                    description = this.description,
                    items = this.items,
                    groups = this.groups,
                    lifecycle = SmobListLifecycle(this.lcStatus, this.lcCompletion),
                ) as ATO
            }
            is SmobProductDTO -> {
                SmobProductATO (
                    id = (this as SmobProductDTO).id,
                    itemStatus = this.itemStatus,
                    itemPosition = this.itemPosition,
                    name = this.name,
                    description = this.description,
                    imageUrl = this.imageUrl,
                    category = ProductCategory(this.categoryMain, this.categorySub),
                    activity = ActivityStatus(this.activityDate, this.activityReps),
                    inShop = InShop(this.inShopCategory, this.inShopName, this.inShopLocation),
                ) as ATO
            }
            is SmobShopDTO -> {
                SmobShopATO (
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
                ) as ATO
            }
            is SmobUserDTO -> {
                SmobUserATO (
                    id = (this as SmobUserDTO).id,
                    itemStatus = this.itemStatus,
                    itemPosition = this.itemPosition,
                    username = this.username,
                    name = this.name,
                    email = this.email,
                    imageUrl = this.imageUrl,
                    groups = this.groups,
                ) as ATO
            }

        }  // when(DTO) ... resolving generic type to concrete type

}
