package com.tanfra.shopmob.features.commonScaffold.presentation.model

import androidx.compose.runtime.Composable

sealed interface ScaffoldMutation {

    // Scaffold mutations
    data class SetGoBackFlag(val daFlag: Boolean) : ScaffoldMutation
    data class SetNewFab(val daFab: (@Composable () -> Unit)?) : ScaffoldMutation
    data class SetNewTitle(val daTitle: String) : ScaffoldMutation
    data object SetPreviousTitle : ScaffoldMutation

    // generic content mutations
    data object ShowLostConnection : ScaffoldMutation
    data object DismissLostConnection : ScaffoldMutation
    data object ShowLoader : ScaffoldMutation
    data class ShowError(val exception: Exception) : ScaffoldMutation

}