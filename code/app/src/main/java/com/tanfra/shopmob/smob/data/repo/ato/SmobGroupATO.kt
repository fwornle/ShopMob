package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.local.utils.ActivityStatus
import com.tanfra.shopmob.smob.data.local.utils.GroupType
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus
import kotlinx.serialization.*

// domain independent data type (Application Transfer Object)
@Serializable
data class SmobGroupATO(
    override val id: String,
    override var itemStatus: @Contextual SmobItemStatus,
    override var itemPosition: Long,
    var name: String,
    var description: String?,
    var type: GroupType,
    var members: List<String>,
    // serialization strategy decided at run-time (@Contextual)
    var activity: @Contextual ActivityStatus,
) : Ato(), java.io.Serializable