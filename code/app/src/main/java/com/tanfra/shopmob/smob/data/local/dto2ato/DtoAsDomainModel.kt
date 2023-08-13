package com.tanfra.shopmob.smob.data.local.dto2ato

import com.tanfra.shopmob.smob.data.local.dto.*
import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.InShop
import com.tanfra.shopmob.smob.data.types.ProductCategory
import com.tanfra.shopmob.smob.data.types.ShopLocation
import com.tanfra.shopmob.smob.data.types.SmobListLifecycle
import com.tanfra.shopmob.smob.data.repo.ato.*

// DTO --> ATO
@Suppress("USELESS_CAST", "UNCHECKED_CAST")
fun <DTO: Dto, ATO: Ato> DTO?._asDomainModel(d: DTO): ATO {

    return when (d as Dto) {

            is SmobGroupDTO -> {

                SmobGroupATO(
                    itemId = (this as SmobGroupDTO).itemId,
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
                    itemId = (this as SmobListDTO).itemId,
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
                    itemId = (this as SmobProductDTO).itemId,
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
                    itemId = (this as SmobShopDTO).itemId,
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
                    itemId = (this as SmobUserDTO).itemId,
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
