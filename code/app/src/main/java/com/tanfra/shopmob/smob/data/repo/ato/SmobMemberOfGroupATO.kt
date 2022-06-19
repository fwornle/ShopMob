package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.local.utils.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

// domain independent data type (Application Transfer Object)
@Serializable
data class SmobMemberOfGroupATO(
    override val id: String,
    override var itemStatus: @Contextual SmobItemStatus,
    override var itemPosition: Long,
    var memberUsername: String,
    var memberName: String,
    var memberEmail: String,
    var memberImageUrl: String?,
    val groupId: String,
    val groupStatus: @Contextual SmobItemStatus,
    val groupPosition: Long,
    var groupName: String,
    var groupDescription: String?,
    var groupType: GroupType,
    var groupMembers: List<String>,
    // serialization strategy decided at run-time (@Contextual)
    var groupActivity: @Contextual ActivityStatus,
) : Ato, java.io.Serializable