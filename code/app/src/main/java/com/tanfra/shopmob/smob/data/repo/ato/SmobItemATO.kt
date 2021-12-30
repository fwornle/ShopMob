package com.tanfra.shopmob.smob.data.repo.ato

import kotlinx.serialization.Serializable
import java.util.*

// domain independent data type (Application Transfer Object)
@Serializable
data class SmobItemATO(
    var title: String?,
    var description: String?,
    var location: String?,
    var latitude: Double?,
    var longitude: Double?,
    val id: String = UUID.randomUUID().toString(),
) : java.io.Serializable