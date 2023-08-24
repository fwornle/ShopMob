package com.tanfra.shopmob.smob.data.net.nto

import com.tanfra.shopmob.smob.data.types.ItemStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


// network data type
@Serializable
@SerialName("smobUserNTO")
class SmobUserNTO(
    override val id: String,
    override val status: ItemStatus,
    override val position: Long,
    val username: String,
    val name: String,
    val email: String,
    val imageUrl: String?,
    val groups: List<String>,
): Nto