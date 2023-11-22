package com.tanfra.shopmob.features.commonScaffold.presentation.model

sealed interface ScaffoldEvent {
    data class Refreshing(val isOn: Boolean) : ScaffoldEvent  // ???
}