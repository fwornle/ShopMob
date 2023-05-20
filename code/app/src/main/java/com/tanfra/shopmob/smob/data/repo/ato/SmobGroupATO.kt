package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.GroupType
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.SmobItemId
import com.tanfra.shopmob.smob.data.types.SmobItemPosition
import com.tanfra.shopmob.smob.data.types.SmobMemberItem
import kotlinx.serialization.*

// domain independent data type (Application Transfer Object)
@Serializable
data class SmobGroupATO(
    override val itemId: @Contextual SmobItemId,
    override var itemStatus: @Contextual ItemStatus,
    override var itemPosition: @Contextual SmobItemPosition,
    var name: String,
    var description: String?,
    var type: GroupType,
    var members: List<SmobMemberItem>,
    // serialization strategy decided at run-time (@Contextual)
    var activity: @Contextual ActivityStatus,
) : Ato, java.io.Serializable