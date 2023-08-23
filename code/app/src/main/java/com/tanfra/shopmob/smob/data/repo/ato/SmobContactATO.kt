package com.tanfra.shopmob.smob.data.repo.ato
// adapted from: https://medium.com/@kednaik/android-contacts-fetching-using-coroutines-aa0129bffdc4

import com.tanfra.shopmob.smob.data.types.ItemStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


// domain independent data type (Application Transfer Object)
@Serializable
@SerialName("smobContact")
data class SmobContactATO(
    override val id: String,
    override var status: ItemStatus,
    override var position: Long,
    var name: String,
    val numbers: MutableList<String> = mutableListOf(),
    val emails: MutableList<String> = mutableListOf(),
) : Ato

