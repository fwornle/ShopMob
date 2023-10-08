package com.tanfra.shopmob.features.smobPlanning.presentation

import androidx.lifecycle.ViewModel
import com.tanfra.shopmob.features.common.arch.ActionProcessor
import com.tanfra.shopmob.features.common.arch.Reducer
import com.tanfra.shopmob.features.common.arch.model
import com.tanfra.shopmob.features.common.dispatcher.DispatcherProvider
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Action
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Event
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Mutation
import com.tanfra.shopmob.features.smobPlanning.presentation.view.ViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlanningViewModelMvi(
    actionProcessors: Collection<ActionProcessor<Action, Mutation, Event>>,
    reducers: Collection<Reducer<Mutation, ViewState>>,
    dispatcherProvider: DispatcherProvider,
    initialState: ViewState = ViewState(),
) : ViewModel() {
    private val model by model(actionProcessors, reducers, dispatcherProvider, initialState)

    internal val viewStateFlow: Flow<ViewState> get() = model.viewStateFlow
    internal val eventFlow: Flow<Event> get() = model.eventFlow

    // refreshing state
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshingSF = _isRefreshing.asStateFlow()

    fun process(action: Action) = model.process(action)
}