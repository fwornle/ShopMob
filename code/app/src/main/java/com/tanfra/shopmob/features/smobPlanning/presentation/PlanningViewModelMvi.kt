package com.tanfra.shopmob.features.smobPlanning.presentation

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlanningViewModelMvi(
    actionProcessors: Collection<ActionProcessor<PlanningAction, PlanningMutation, PlanningEvent>>,
    reducers: Collection<Reducer<PlanningMutation, PlanningViewState>>,
    dispatcherProvider: DispatcherProvider,
    initialState: PlanningViewState = PlanningViewState(),
) : ViewModel() {
    private val model by model(actionProcessors, reducers, dispatcherProvider, initialState)

    internal val viewStateFlow: Flow<PlanningViewState> get() = model.viewStateFlow
    internal val eventFlow: Flow<PlanningEvent> get() = model.eventFlow

    // refreshing state
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshingSF = _isRefreshing.asStateFlow()

    fun process(action: PlanningAction) = model.process(action)
}