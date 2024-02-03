package com.tanfra.shopmob.features.commonScaffold.presentation

import androidx.compose.runtime.Composable
import com.tanfra.shopmob.features.common.arch.ActionProcessor
import com.tanfra.shopmob.features.common.monitor.ConnectivityMonitor
import com.tanfra.shopmob.features.common.monitor.ConnectivityStatus
import com.tanfra.shopmob.features.commonScaffold.presentation.model.ScaffoldAction
import com.tanfra.shopmob.features.commonScaffold.presentation.model.ScaffoldEvent
import com.tanfra.shopmob.features.commonScaffold.presentation.model.ScaffoldMutation
import com.tanfra.shopmob.features.commonScaffold.presentation.view.TopLevelDestination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class ScaffoldActionProcessor(
    private val connectivityMonitor: ConnectivityMonitor,
) : ActionProcessor<ScaffoldAction, ScaffoldMutation, ScaffoldEvent> {

    override fun invoke(action: ScaffoldAction): Flow<Pair<ScaffoldMutation?, ScaffoldEvent?>> =
        if (action is ScaffoldAction.CheckConnectivity) {
            checkConnectivity()
        } else flow {
            when (action) {

                // Scaffold management
                is ScaffoldAction.SetNewScaffold -> setNewScaffold(
                    action.newTitle,
                    action.newGoBackFlag,
                    action.newFab
                )
                ScaffoldAction.SetPreviousScaffold-> setPreviousScaffold()
                is ScaffoldAction.ResetToScaffold -> resetToScaffold(
                    action.newTitle,
                    action.newGoBackFlag,
                    action.newFab
                )
                is ScaffoldAction.SetNewFab -> setNewFab(action.newFab)
                is ScaffoldAction.SetNewTopLevelDest -> setTopLevelDestination(action.daDest)

                // generic
                else -> {
                    //no-op
                    Timber.i("MVI.UI: ${action.toString().take(50)}... not found in ScaffoldActionProcessor")
                }
            }
        }


    // Actions ---------------------------------------------------------------------------
    // Actions ---------------------------------------------------------------------------
    // Actions ---------------------------------------------------------------------------

    // set new Scaffold parameters (title, GoBack icon, FAB)
    private suspend fun FlowCollector<Pair<ScaffoldMutation?, ScaffoldEvent?>>.setNewScaffold(
        daTitle : String,
        daFlag: Boolean,
        daFab: (@Composable () -> Unit)?,
    ) = emit(ScaffoldMutation.SetNewScaffold(
        daTitle = daTitle,
        daFlag = daFlag,
        daFab = daFab
    ) to null)

    // set previous Scaffold parameters
    private suspend fun FlowCollector<Pair<ScaffoldMutation?, ScaffoldEvent?>>
            .setPreviousScaffold() = emit(ScaffoldMutation.SetPreviousScaffold to null)

    // reset Scaffold parameters to provided values
    private suspend fun FlowCollector<Pair<ScaffoldMutation?, ScaffoldEvent?>>.resetToScaffold(
        daTitle : String,
        daFlag: Boolean,
        daFab: (@Composable () -> Unit)?,
    ) = emit(ScaffoldMutation.ResetToScaffold(
        daTitle = daTitle,
        daFlag = daFlag,
        daFab = daFab
    ) to null)

    // set new Fab in Scaffold
    private suspend fun FlowCollector<Pair<ScaffoldMutation?, ScaffoldEvent?>>.setNewFab(
        daFab : (@Composable () -> Unit)?
    ) = emit(ScaffoldMutation.SetNewFab(daFab) to null)

    // set new destination in Scaffold (sync-ed "active" route in BottomBar and NavDrawer)
    private suspend fun FlowCollector<Pair<ScaffoldMutation?, ScaffoldEvent?>>.setTopLevelDestination(
        daDest : TopLevelDestination?
    ) = emit(ScaffoldMutation.SetTopLevelDest(daDest) to null)

    private fun checkConnectivity() =
        connectivityMonitor.statusFlow
            .map { status ->
                if (status == ConnectivityStatus.AVAILABLE) {
                    ScaffoldMutation.DismissLostConnection
                } else {
                    ScaffoldMutation.ShowLostConnection
                } to null
            }

}

