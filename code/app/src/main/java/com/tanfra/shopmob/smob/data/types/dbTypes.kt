package com.tanfra.shopmob.smob.data.types

import kotlinx.serialization.Serializable

@Serializable
data class InShop(
    val category: ShopCategory,
    val name: String,
    val location: ShopLocation,
)

@Serializable
data class ActivityStatus(
    var date: String,
    var reps: Long,
)

@Serializable
data class ProductCategory(
    var main: ProductMainCategory,
    var sub: ProductSubCategory,
)

@Serializable
data class ShopLocation(
    val latitude: Double,
    val longitude: Double,
)

@Serializable
data class SmobListItem(
    val id: String,
    var status: ItemStatus,
    var listPosition: Long,
    val mainCategory: ProductMainCategory,
)

@Serializable
data class SmobMemberItem(
    val id: String,
    var status: ItemStatus,
    var listPosition: Long,
)

@Serializable
data class SmobGroupItem(
    val id: String,
    var status: ItemStatus,
    var listPosition: Long,
)

@Serializable
data class SmobListLifecycle(
    var status: ItemStatus,
    var completion: Double
)
