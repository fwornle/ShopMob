package com.tanfra.shopmob.features.commonScaffold.presentation.model

import androidx.compose.runtime.Composable

sealed interface ScaffoldAction {

    // scaffold management
    data class SetGoBackFlag(val newFlag: Boolean): ScaffoldAction
    data class SetNewFab(val newFab: (@Composable () -> Unit)?) : ScaffoldAction
    data class SetNewTitle(val newTitle: String): ScaffoldAction
    data object SetPreviousTitle : ScaffoldAction

    // default actions
    data object CheckConnectivity : ScaffoldAction

}