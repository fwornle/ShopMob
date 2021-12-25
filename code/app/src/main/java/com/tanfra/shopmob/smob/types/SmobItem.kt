package com.tanfra.shopmob.smob.types

import kotlinx.serialization.Serializable
import java.util.*

// domain independent data type
@Serializable
data class SmobItem(
    var title: String?,
    var description: String?,
    var location: String?,
    var latitude: Double?,
    var longitude: Double?,
    val id: String = UUID.randomUUID().toString()
) : java.io.Serializable