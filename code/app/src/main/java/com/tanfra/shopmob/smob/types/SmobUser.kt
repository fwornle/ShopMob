package com.tanfra.shopmob.smob.types
import kotlinx.serialization.Serializable

// domain independent data type
@Serializable
data class SmobUser(
    val id: String,
    var username: String,
    var name: String,
    var email: String,
    var imageUrl: String?,
) : java.io.Serializable