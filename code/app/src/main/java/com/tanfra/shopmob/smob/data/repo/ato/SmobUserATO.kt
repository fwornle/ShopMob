package com.tanfra.shopmob.smob.data.repo.ato
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

// domain independent data type (Application Transfer Object)
@Serializable
data class SmobUserATO(
    override val id: String,
    override var itemStatus: @Contextual SmobItemStatus,
    override var itemPosition: Long,
    var username: String,
    var name: String,
    var email: String,
    var imageUrl: String?,
) : Ato, java.io.Serializable