package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.local.utils.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

// domain independent data type (Application Transfer Object)
@Serializable
data class SmobProductOnListATO(
    override val id: String,
    override var itemStatus: @Contextual SmobItemStatus,
    override var itemPosition: Long,
    var productName: String,
    var productDescription: String?,
    var productImageUrl: String?,
    var productCategory: @Contextual ProductCategory,
    var productActivity: @Contextual ActivityStatus,
    val listId: String,
    val listStatus: @Contextual SmobItemStatus,
    val listPosition: Long,
    var listName: String,
    var listDescription: String?,
    var listItems: List<@Contextual SmobListItem>,
    var listGroups: List<SmobGroupItem>,
    var listLifecycle: @Contextual SmobListLifecycle,
) : Ato, java.io.Serializable