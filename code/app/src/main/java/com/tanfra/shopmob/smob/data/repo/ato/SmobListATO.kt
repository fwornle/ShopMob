package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.local.utils.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

// domain independent data type (Application Transfer Object)
@Serializable
data class SmobListATO(
    override val itemId: String,
    override var itemStatus: @Contextual SmobItemStatus,
    override var itemPosition: Long,
    var name: String,
    var description: String?,
    var items: List<@Contextual SmobListItem>,
    var groups: List<SmobGroupItem>,
    var lifecycle: @Contextual SmobListLifecycle,
) : Ato, java.io.Serializable