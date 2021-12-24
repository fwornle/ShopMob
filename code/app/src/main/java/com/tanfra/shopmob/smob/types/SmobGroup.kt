package com.tanfra.shopmob.smob.types
import com.tanfra.shopmob.utils.ActivityState
import com.tanfra.shopmob.utils.GroupType
import kotlinx.serialization.*

// domain independent data type
@Serializable
data class SmobGroup(
    val id: String,
    var name: String,
    var description: String?,
    var type: GroupType,
    var members: List<String?>,
    // serialization strategy decided at run-time (@Contextual)
    var activityState: @Contextual ActivityState,
) : java.io.Serializable