package com.tanfra.shopmob.smob.data.repo.ato
import kotlinx.serialization.Serializable

// domain independent data type (Application Transfer Object)
@Serializable
data class SmobUserATO(
    override val id: String,
    var username: String,
    var name: String,
    var email: String,
    var imageUrl: String?,
) : Ato(), java.io.Serializable