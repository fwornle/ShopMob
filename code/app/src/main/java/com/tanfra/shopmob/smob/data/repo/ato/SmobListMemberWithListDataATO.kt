package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.local.utils.*
import com.tanfra.shopmob.smob.data.repo.utils.Member
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

// domain independent data type (Application Transfer Object)
@Serializable
data class SmobListMemberWithListDataATO(
    override val id: String,
    override var itemStatus: @Contextual SmobItemStatus,
    override var itemPosition: Long,
    var memberUsername: String,
    var memberName: String,
    var memberEmail: String,
    var memberImageUrl: String?,
    val listId: String,
    val listStatus: @Contextual SmobItemStatus,
    val listPosition: Long,
    var listName: String,
    var listDescription: String?,
    var listItems: List<@Contextual SmobListItem>,
    var listMembers: List<@Contextual SmobMemberItem>,
    // serialization strategy decided at run-time (@Contextual)
    var listLifecycle: @Contextual SmobListLifecycle,
) : Ato, java.io.Serializable {
    fun member() = Member(this.memberName, this.memberUsername, this.memberEmail, this.memberImageUrl)
}