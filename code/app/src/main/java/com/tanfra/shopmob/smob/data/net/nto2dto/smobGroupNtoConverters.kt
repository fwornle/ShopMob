package com.tanfra.shopmob.smob.data.net.nto2dto

import com.tanfra.shopmob.smob.data.local.dto.SmobGroupDTO
import com.tanfra.shopmob.smob.data.net.nto.SmobGroupNTO
import com.tanfra.shopmob.smob.data.local.utils.ActivityStatus

// extension functions to convert between database types and domain data types (both directions)

// ArrayList<SmobGroupNTO> --> List<SmobGroupDTO>
fun ArrayList<SmobGroupNTO>.asRepoModel(): List<SmobGroupDTO> {
    return this.let {
        it.map {
            SmobGroupDTO(
                id = it.id,
                itemStatus = it.itemStatus,
                itemPosition = it.itemPosition,
                name = it.name,
                description = it.description,
                type = it.type,
                members = it.members,
                activityDate = it.activity.date,
                activityReps = it.activity.reps,
            )
        }
    }
}

// List<SmobGroupDTO> --> ArrayList<SmobGroupNTO>
fun List<SmobGroupDTO>.asNetworkModel(): ArrayList<SmobGroupNTO> {
    return ArrayList(
        map {
            SmobGroupNTO(
                id = it.id,
                itemStatus = it.itemStatus,
                itemPosition = it.itemPosition,
                name = it.name,
                description = it.description,
                type = it.type,
                members = it.members,
                activity = ActivityStatus(it.activityDate, it.activityReps),
            )
        },
    )
}

// SmobGroupNTO --> SmobGroupDTO
fun SmobGroupNTO.asRepoModel(): SmobGroupDTO {
    return SmobGroupDTO (
        id = this.id,
        itemStatus = this.itemStatus,
        itemPosition = this.itemPosition,
        name = this.name,
        description = this.description,
        type = this.type,
        members = this.members,
        activityDate = this.activity.date,
        activityReps = this.activity.reps,
    )
}

// SmobGroupDTO --> SmobGroupNTO
fun SmobGroupDTO.asNetworkModel(): SmobGroupNTO {
    return SmobGroupNTO (
        id = this.id,
        itemStatus = this.itemStatus,
        itemPosition = this.itemPosition,
        name = this.name,
        description = this.description,
        type = this.type,
        members = this.members,
        activity = ActivityStatus(this.activityDate, this.activityReps),
    )
}