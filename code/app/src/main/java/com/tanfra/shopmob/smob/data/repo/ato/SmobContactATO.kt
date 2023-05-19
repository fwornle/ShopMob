package com.tanfra.shopmob.smob.data.repo.ato
// adapted from: https://medium.com/@kednaik/android-contacts-fetching-using-coroutines-aa0129bffdc4

import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

// domain independent data type (Application Transfer Object)
@Serializable
data class SmobContactATO(
    override val itemId: String,
    override var itemStatus: @Contextual SmobItemStatus,
    override var itemPosition: Long,
    var name: String,
    val numbers: MutableList<String> = mutableListOf(),
    val emails: MutableList<String> = mutableListOf(),
) : Ato, java.io.Serializable

