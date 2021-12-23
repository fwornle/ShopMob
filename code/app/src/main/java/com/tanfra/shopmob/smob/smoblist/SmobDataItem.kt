package com.tanfra.shopmob.smob.smoblist

import kotlinx.serialization.Serializable
import java.util.*

/**
 * data class acts as a data mapper between the DB and the UI
 */
@Serializable
data class SmobDataItem(
    var title: String?,
    var description: String?,
    var location: String?,
    var latitude: Double?,
    var longitude: Double?,
    val id: String = UUID.randomUUID().toString()
) : java.io.Serializable