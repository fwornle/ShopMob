package com.tanfra.shopmob.smob.data.types

import kotlinx.serialization.Serializable

@Serializable
data class InShop(
    val category: ShopCategory,
    val name: String,
    val location: ShopLocation,
) : java.io.Serializable

@Serializable
data class ActivityStatus(
    val date: String,
    val reps: Long,
) : java.io.Serializable

@Serializable
data class ProductCategory(
    var main: ProductMainCategory,
    var sub: ProductSubCategory,
) : java.io.Serializable

@Serializable
data class ShopLocation(
    val latitude: Double,
    val longitude: Double,
) : java.io.Serializable

@Serializable
data class SmobListItem(
    val id: String,
    var status: ItemStatus,
    val listPosition: Long,
    val mainCategory: ProductMainCategory,
) : java.io.Serializable

@Serializable
data class SmobMemberItem(
    val id: String,
    var status: ItemStatus,
    var listPosition: Long,
) : java.io.Serializable

@Serializable
data class SmobGroupItem(
    val id: String,
    var status: ItemStatus,
    val listPosition: Long,
) : java.io.Serializable

@Serializable
data class SmobListLifecycle(
    val status: ItemStatus,
    var completion: Double
) : java.io.Serializable
