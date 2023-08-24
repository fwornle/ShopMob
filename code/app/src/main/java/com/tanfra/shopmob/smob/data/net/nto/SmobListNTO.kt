package com.tanfra.shopmob.smob.data.net.nto

import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.SmobGroupItem
import com.tanfra.shopmob.smob.data.types.SmobListItem
import com.tanfra.shopmob.smob.data.types.SmobListLifecycle
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


// network data type
@Serializable
@SerialName("smobListNTO")
data class SmobListNTO(
    override val id: String,
    override val status: ItemStatus,
    override val position: Long,
    val name: String,
    val description: String?,
    val items: List<SmobListItem>,
    val groups: List<SmobGroupItem>,
    val lifecycle: SmobListLifecycle,
): Nto
