package com.tanfra.shopmob.features.smobPlanning.presentation.model

sealed interface Event {
    data class Refreshing(val isOn: Boolean) : Event  // ???
    data object NavigateBack : Event
}