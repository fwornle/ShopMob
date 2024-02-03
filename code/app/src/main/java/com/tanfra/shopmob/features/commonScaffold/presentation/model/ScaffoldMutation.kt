package com.tanfra.shopmob.features.commonScaffold.presentation.model

import androidx.compose.runtime.Composable
import com.tanfra.shopmob.features.commonScaffold.presentation.view.TopLevelDestination

sealed interface ScaffoldMutation {

    // Scaffold mutations
    data class SetNewScaffold(
        val daTitle: String,
        val daFlag: Boolean,
        val daFab: (@Composable () -> Unit)?
    ) : ScaffoldMutation
    data object SetPreviousScaffold : ScaffoldMutation
    data class ResetToScaffold(
        val daTitle: String,
        val daFlag: Boolean,
        val daFab: (@Composable () -> Unit)?
    ) : ScaffoldMutation
    data class SetNewFab(val daFab: (@Composable () -> Unit)?) : ScaffoldMutation
    data class SetTopLevelDest(val daDest: TopLevelDestination?) : ScaffoldMutation

    // generic content mutations
    data object ShowLostConnection : ScaffoldMutation
    data object DismissLostConnection : ScaffoldMutation
    data object ShowLoader : ScaffoldMutation
    data class ShowError(val exception: Exception) : ScaffoldMutation

}