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
    override val id: String,
    override var status: ItemStatus,
    override var position: Long,
    var name: String,
    var description: String?,
    var items: List<SmobListItem>,
    var groups: List<SmobGroupItem>,
    var lifecycle: SmobListLifecycle,
) : Ato