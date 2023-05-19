package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.types.ItemStatus
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

// domain independent data type (Application Transfer Object)
@Serializable
data class SmobUserATO(
    override val itemId: String,
    override var itemStatus: @Contextual ItemStatus,
    override var itemPosition: Long,
    var username: String,
    var name: String,
    var email: String,
    var imageUrl: String?,
    var groups: List<String>,
) : Ato, java.io.Serializable {

    // determine, if user is affiliated with any groups (yet)
    fun hasGroupRefs(): Boolean = this.groups.filter { it != "" }.any()

}
