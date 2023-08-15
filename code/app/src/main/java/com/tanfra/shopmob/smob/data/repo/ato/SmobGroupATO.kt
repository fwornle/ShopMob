package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.GroupType
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.SmobMemberItem
import kotlinx.serialization.*

// domain independent data type (Application Transfer Object)
@Serializable
data class SmobGroupATO(
    override val id: String,
    override var status: @Contextual ItemStatus,
    override var position: Long,
    var name: String,
    var description: String?,
    var type: GroupType,
    var members: List<SmobMemberItem>,
    // serialization strategy decided at run-time (@Contextual)
    var activity: @Contextual ActivityStatus,
) : Ato, java.io.Serializable