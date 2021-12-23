package com.tanfra.shopmob.smob.data.net

import com.squareup.moshi.Json

data class SmodUserProfilePicture(
    @Json(name = "media_type") val mediaType: String,
    val title: String,
    val url: String
    )