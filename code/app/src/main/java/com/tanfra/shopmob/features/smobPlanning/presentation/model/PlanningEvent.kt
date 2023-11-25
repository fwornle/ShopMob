package com.tanfra.shopmob.features.smobPlanning.presentation.model

import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO

sealed interface PlanningEvent {
    data object SampleEvent : PlanningEvent
    data class NavigateToList(val list: SmobListATO) : PlanningEvent
    data object NavigateBack : PlanningEvent
}