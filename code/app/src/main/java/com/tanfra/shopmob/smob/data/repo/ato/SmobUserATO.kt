package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.app.Constants.INVALID_SMOB_ITEM_ID
import com.tanfra.shopmob.smob.data.types.ItemStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// domain independent data type (Application Transfer Object)
@Serializable
@SerialName("smobUserATO")
data class SmobUserATO(
    override val id: String = INVALID_SMOB_ITEM_ID,
    override var status: ItemStatus = ItemStatus.INVALID,
    override var position: Long = -1,
    val username: String = "invalid user name",
    val name: String = "invalid name",
    val email: String = "",
    val imageUrl: String? = null,
    var groups: List<String> = listOf(),
) : Ato {

    // determine, if user is affiliated with any groups (yet)
    fun hasGroupRefs(): Boolean = this.groups.filter { it != "" }.any()

}
