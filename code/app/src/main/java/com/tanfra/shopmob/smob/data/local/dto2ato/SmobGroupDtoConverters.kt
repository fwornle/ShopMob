package com.tanfra.shopmob.smob.data.local.dto2ato

import com.tanfra.shopmob.smob.data.local.dto.SmobGroupDTO
import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupATO
import com.tanfra.shopmob.smob.data.local.utils.ActivityStatus

// extension functions to convert between database types and domain data types (both directions)

// List<SmobGroupDTO> --> List<SmobGroup>
fun List<SmobGroupDTO>.asDomainModel(): List<SmobGroupATO> {
    return map {
        SmobGroupATO (
            id = it.id,
            name = it.name,
            description = it.description,
            type = it.type,
            members = it.members,
            activity = ActivityStatus(it.activityDate, it.activityReps),
        )
    }
}

// List<SmobGroup> --> List<SmobGroupDTO>
fun List<SmobGroupATO>.asDatabaseModel(): List<SmobGroupDTO> {
    return map {
        SmobGroupDTO (
            id = it.id,
            name = it.name,
            description = it.description,
            type = it.type,
            members = it.members,
            activityDate = it.activity.date,
            activityReps = it.activity.reps,
        )
    }
}

// SmobGroupDTO --> SmobGroup
fun SmobGroupDTO.asDomainModel(): SmobGroupATO {
    return SmobGroupATO (
        id = this.id,
        name = this.name,
        description = this.description,
        type = this.type,
        members = this.members,
        activity = ActivityStatus(this.activityDate, this.activityReps),
    )
}

// SmobGroup --> SmobGroupDTO
fun SmobGroupATO.asDatabaseModel(): SmobGroupDTO {
    return SmobGroupDTO (
        id = this.id,
        name = this.name,
        description = this.description,
        type = this.type,
        members = this.members,
        activityDate = this.activity.date,
        activityReps = this.activity.reps,
    )
}