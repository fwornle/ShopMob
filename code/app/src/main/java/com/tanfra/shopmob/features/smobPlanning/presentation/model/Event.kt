package com.tanfra.shopmob.features.smobPlanning.presentation.model

import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO

sealed interface Event {
    data class Refreshing(val isOn: Boolean) : Event  // ???
    data class NavigateToList(val list: SmobListATO) : Event
    data object NavigateBack : Event
}