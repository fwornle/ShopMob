package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.types.ItemStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


// domain independent data type (Application Transfer Object)
@Serializable
@SerialName("smobUser")
data class SmobUserATO(
    override val id: String,
    override var status: ItemStatus,
    override var position: Long,
    var username: String,
    var name: String,
    var email: String,
    var imageUrl: String?,
    var groups: List<String>,
) : Ato {

    // determine, if user is affiliated with any groups (yet)
    fun hasGroupRefs(): Boolean = this.groups.filter { it != "" }.any()

}
