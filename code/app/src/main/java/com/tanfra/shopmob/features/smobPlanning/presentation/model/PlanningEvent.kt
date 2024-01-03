package com.tanfra.shopmob.features.smobPlanning.presentation.model

import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO

sealed interface PlanningEvent {
    data object SampleEvent : PlanningEvent
    data class NavigateToList(val list: SmobListATO) : PlanningEvent
    data class NavigateToShop(val shop: SmobShopATO) : PlanningEvent
    data object NavigateBack : PlanningEvent
}