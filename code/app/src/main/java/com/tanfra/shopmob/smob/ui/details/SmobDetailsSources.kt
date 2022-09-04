package com.tanfra.shopmob.smob.ui.details

import kotlinx.serialization.Serializable

// valid sources for intent based navigation to the SmobDetailsActivity
@Serializable
enum class SmobDetailsSources {
    UNKNOWN,
    PLANNING_PRODUCT_LIST,
    PLANNING_SHOP_LIST,
    GEOFENCE,
    FCMUPDATE,
    SHOPPING,
    ADMINISTRATION,
    DETAILS,
}