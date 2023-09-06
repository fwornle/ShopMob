package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.SmobGroupItem
import com.tanfra.shopmob.smob.data.types.SmobListItem
import com.tanfra.shopmob.smob.data.types.SmobListLifecycle
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// domain independent data type (Application Transfer Object)
@Serializable
@SerialName("smobListATO")
data class SmobListATO(
    override val id: String = "invalid id",
    override var status: ItemStatus = ItemStatus.INVALID,
    override var position: Long = -1,
    val name: String = "invalidName",
    val description: String? = null,
    val items: List<SmobListItem> = listOf(),
    var groups: List<SmobGroupItem> = listOf(),
    val lifecycle: SmobListLifecycle = SmobListLifecycle(ItemStatus.INVALID, 0.0),
) : Ato