package com.tanfra.shopmob.utils

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.tanfra.shopmob.smob.data.local.dto.SmobGroupDTO
import com.tanfra.shopmob.smob.types.SmobGroup
import java.util.*

// extension functions to convert between database types and domain data types (both directions)

// List<SmobGroupDTO> --> List<SmobGroup>
fun List<SmobGroupDTO>.asDomainModel(): List<SmobGroup> {
    return map {
        SmobGroup (
            id = it.groupId,
            name = it.name,
            description = it.description,
            type = it.type,
            members = it.members,
            activityState = it.activityState,
        )
    }
}

// List<SmobGroup> --> List<SmobGroupDTO>
fun List<SmobGroup>.asDatabaseModel(): List<SmobGroupDTO> {
    return map {
        SmobGroupDTO (
            groupId = it.id,
            name = it.name,
            description = it.description,
            type = it.type,
            members = it.members,
            activityState = it.activityState,
        )
    }
}

// SmobGroupDTO --> SmobGroup
fun SmobGroupDTO.asDomainModel(): SmobGroup {
    return SmobGroup (
        id = this.groupId,
        name = this.name,
        description = this.description,
        type = this.type,
        members = this.members,
        activityState = this.activityState,
    )
}

// SmobGroup --> SmobGroupDTO
fun SmobGroup.asDatabaseModel(): SmobGroupDTO {
    return SmobGroupDTO (
        groupId = this.id,
        name = this.name,
        description = this.description,
        type = this.type,
        members = this.members,
        activityState = this.activityState,
    )
}