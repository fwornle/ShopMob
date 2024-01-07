package com.tanfra.shopmob.features.commonScaffold.presentation

import androidx.compose.runtime.Composable
import com.tanfra.shopmob.features.common.arch.ActionProcessor
import com.tanfra.shopmob.features.common.monitor.ConnectivityMonitor
import com.tanfra.shopmob.features.common.monitor.ConnectivityStatus
import com.tanfra.shopmob.features.commonScaffold.presentation.model.ScaffoldAction
import com.tanfra.shopmob.features.commonScaffold.presentation.model.ScaffoldEvent
import com.tanfra.shopmob.features.commonScaffold.presentation.model.ScaffoldMutation
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
                is ScaffoldAction.SetNewFab -> setNewFab(action.newFab)

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

    // set new title in TopAppBar of Scaffold
    private suspend fun FlowCollector<Pair<ScaffoldMutation?, ScaffoldEvent?>>
            .setPreviousScaffold() = emit(ScaffoldMutation.SetPreviousScaffold to null)

    // set new Fab in Scaffold
    private suspend fun FlowCollector<Pair<ScaffoldMutation?, ScaffoldEvent?>>.setNewFab(
        daFab : (@Composable () -> Unit)?
    ) = emit(ScaffoldMutation.SetNewFab(daFab) to null)

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

