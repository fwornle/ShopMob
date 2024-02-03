package com.tanfra.shopmob.features.commonScaffold.presentation.model

import androidx.compose.runtime.Composable
import com.tanfra.shopmob.features.commonScaffold.presentation.view.TopLevelDestination

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
    data class SetNewTopLevelDest(val daDest: TopLevelDestination?) : ScaffoldAction

    // default actions
    data object CheckConnectivity : ScaffoldAction

}