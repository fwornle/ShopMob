package com.tanfra.shopmob.features.commonScaffold.presentation.model

import androidx.compose.runtime.Composable

sealed interface ScaffoldAction {

    // scaffold management
    data class SetNewScaffold(
        val newTitle: String,
        val newGoBackFlag: Boolean,
        val newFab: (@Composable () -> Unit)?,
    ): ScaffoldAction
    data object SetPreviousScaffold : ScaffoldAction
    data class ResetToScaffold(
        val newTitle: String,
        val newGoBackFlag: Boolean,
        val newFab: (@Composable () -> Unit)?,
    ): ScaffoldAction
    data class SetNewFab(val newFab: (@Composable () -> Unit)?) : ScaffoldAction

    // default actions
    data object CheckConnectivity : ScaffoldAction

}