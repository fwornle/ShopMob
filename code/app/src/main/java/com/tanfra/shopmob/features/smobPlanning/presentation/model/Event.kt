package com.tanfra.shopmob.features.smobPlanning.presentation.model

import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupATO

sealed interface Event {
    data class Refreshing(val isOn: Boolean) : Event  // ???
    data class GroupsLoaded(val groups: List<SmobGroupATO>) : Event

    // further events...
}