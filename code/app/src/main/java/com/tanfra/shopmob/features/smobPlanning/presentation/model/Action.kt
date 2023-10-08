package com.tanfra.shopmob.features.smobPlanning.presentation.model

import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO

sealed interface Action {
    data object CheckConnectivity : Action
    data object LoadLists : Action
    data object ReloadLists : Action
    data object LoadGroups : Action
    data class ConfirmSwipe(val item: SmobListATO) : Action
//    data class PhoneClick(val uuid: String) : Action
}