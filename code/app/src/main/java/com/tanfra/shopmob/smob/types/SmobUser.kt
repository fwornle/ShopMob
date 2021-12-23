package com.tanfra.shopmob.smob.types
import kotlinx.serialization.Serializable

// domain independent data type
@Serializable
data class SmobUser(
    val id: String,
    var name: String,
    var imageUrl: String?,
    var shops: List<String?>,
    var groups: List<String?>,
    var lists: List<String?>,
)