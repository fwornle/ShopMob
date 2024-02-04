package com.tanfra.shopmob.features.smobAdmin.presentation.model

import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO

sealed interface AdminEvent {
    data class NavigateToList(val list: SmobListATO) : AdminEvent
    data class NavigateToGroup(val shop: SmobGroupATO) : AdminEvent
    data object NavigateBack : AdminEvent
}