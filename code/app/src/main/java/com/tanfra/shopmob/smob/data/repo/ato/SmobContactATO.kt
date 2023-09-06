package com.tanfra.shopmob.smob.data.repo.ato
// adapted from: https://medium.com/@kednaik/android-contacts-fetching-using-coroutines-aa0129bffdc4

import com.tanfra.shopmob.smob.data.types.ItemStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// domain independent data type (Application Transfer Object)
@Serializable
@SerialName("smobContactATO")
data class SmobContactATO(
    override val id: String = "invalid ID",
    override var status: ItemStatus = ItemStatus.INVALID,
    override var position: Long = -1,
    val name: String = "invalid name",
    val numbers: MutableList<String> = mutableListOf(),
    val emails: MutableList<String> = mutableListOf(),
) : Ato

