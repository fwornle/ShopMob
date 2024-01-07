package com.tanfra.shopmob.features.smobPlanning.presentation

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import com.tanfra.shopmob.features.common.arch.ActionProcessor
import com.tanfra.shopmob.features.common.arch.Reducer
import com.tanfra.shopmob.features.common.arch.model
import com.tanfra.shopmob.features.common.dispatcher.DispatcherProvider
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningAction
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningEvent
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningMutation
import com.tanfra.shopmob.features.smobPlanning.presentation.view.PlanningViewState
import kotlinx.coroutines.flow.Flow

// VM declared STABLE, as VM consumers will be notified when any contents change (via observers)
@Stable
class PlanningViewModelMvi(
    actionProcessors: Collection<ActionProcessor<PlanningAction, PlanningMutation, PlanningEvent>>,
    reducers: Collection<Reducer<PlanningMutation, PlanningViewState>>,
    dispatcherProvider: DispatcherProvider,
    initialState: PlanningViewState = PlanningViewState(),
) : ViewModel() {
    private val model by model(actionProcessors, reducers, dispatcherProvider, initialState)

    internal val viewStateFlow: Flow<PlanningViewState> get() = model.viewStateFlow
    internal val eventFlow: Flow<PlanningEvent> get() = model.eventFlow

    fun process(action: PlanningAction) = model.process(action)
}