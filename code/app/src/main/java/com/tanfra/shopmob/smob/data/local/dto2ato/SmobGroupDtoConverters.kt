package com.tanfra.shopmob.smob.data.local.dto2ato

import com.tanfra.shopmob.smob.data.local.dto.SmobGroupDTO
import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupATO
import com.tanfra.shopmob.smob.data.local.utils.ActivityStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

// extension functions to convert between database types and domain data types (both directions)

// Flow<List<SmobGroupDTO>> --> Flow<List<SmobGroupATO>>
fun Flow<List<SmobGroupDTO>>.asDomainModel(): Flow<List<SmobGroupATO>> = transform {
        value ->
    emit(
        value.map {
            SmobGroupATO (
                id = it.itemId,
                itemStatus = it.itemStatus,
                itemPosition = it.itemPosition,
                name = it.name,
                description = it.description,
                type = it.type,
                members = it.members,
                activity = ActivityStatus(it.activityDate, it.activityReps),
            )
        }
    )
}

// List<SmobGroupATO> --> List<SmobGroupDTO>
fun List<SmobGroupATO>.asDatabaseModel(): List<SmobGroupDTO> {
    return map {
        SmobGroupDTO (
            itemId = it.id,
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

// Flow<SmobGroupDTO?> --> Flow<SmobGroupATO?>
// ... need to add an annotation to avoid a clash at byte-code level (same signature as List<> case)
@JvmName("asDomainModelSmobGroupDTO")
fun Flow<SmobGroupDTO?>.asDomainModel(): Flow<SmobGroupATO?> = transform {
        value ->
    emit(
        value?.let {
            SmobGroupATO(
                id = it.itemId,
                itemStatus = it.itemStatus,
                itemPosition = it.itemPosition,
                name = it.name,
                description = it.description,
                type = it.type,
                members = it.members,
                activity = ActivityStatus(it.activityDate, it.activityReps),
            )
        }
    )
}

// SmobGroupATO? --> SmobGroupDTO?
fun SmobGroupATO.asDatabaseModel(): SmobGroupDTO {
    return this.let {
        SmobGroupDTO(
            itemId = it.id,
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
