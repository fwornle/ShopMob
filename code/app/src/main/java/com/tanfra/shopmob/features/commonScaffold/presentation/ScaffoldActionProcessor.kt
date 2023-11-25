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

class ScaffoldActionProcessor(
    private val connectivityMonitor: ConnectivityMonitor,
) : ActionProcessor<ScaffoldAction, ScaffoldMutation, ScaffoldEvent> {

    override fun invoke(action: ScaffoldAction): Flow<Pair<ScaffoldMutation?, ScaffoldEvent?>> =
        if (action is ScaffoldAction.CheckConnectivity) {
            checkConnectivity()
        } else flow {
            when (action) {

                // Scaffold management
                is ScaffoldAction.SetGoBackFlag -> setGoBackFlag(action.newFlag)
                is ScaffoldAction.SetNewFab -> setNewFab(action.newFab)
                is ScaffoldAction.SetNewTitle -> setNewTitle(action.newTitle)
                ScaffoldAction.SetPreviousTitle -> setPreviousTitle()

                // generic
                else -> {
                    //no-op
                }
            }
        }


    // Actions ---------------------------------------------------------------------------
    // Actions ---------------------------------------------------------------------------
    // Actions ---------------------------------------------------------------------------

    // set new state of GoBack flag in Scaffold
    private suspend fun FlowCollector<Pair<ScaffoldMutation?, ScaffoldEvent?>>.setGoBackFlag(
        daFlag : Boolean
    ) = emit(ScaffoldMutation.SetGoBackFlag(daFlag) to null)

    // set new Fab in Scaffold
    private suspend fun FlowCollector<Pair<ScaffoldMutation?, ScaffoldEvent?>>.setNewFab(
        daFab : (@Composable () -> Unit)?
    ) = emit(ScaffoldMutation.SetNewFab(daFab) to null)

    // set new title in TopAppBar of Scaffold
    private suspend fun FlowCollector<Pair<ScaffoldMutation?, ScaffoldEvent?>>.setNewTitle(
        daTitle : String
    ) = emit(ScaffoldMutation.SetNewTitle(daTitle) to null)

    // set new title in TopAppBar of Scaffold
    private suspend fun FlowCollector<Pair<ScaffoldMutation?, ScaffoldEvent?>>.setPreviousTitle() =
        emit(ScaffoldMutation.SetPreviousTitle to null)

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

