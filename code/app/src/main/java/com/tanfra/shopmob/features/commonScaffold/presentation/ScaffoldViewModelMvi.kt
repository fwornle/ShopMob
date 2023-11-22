package com.tanfra.shopmob.features.commonScaffold.presentation

import androidx.lifecycle.ViewModel
import com.tanfra.shopmob.features.common.arch.ActionProcessor
import com.tanfra.shopmob.features.common.arch.Reducer
import com.tanfra.shopmob.features.common.arch.model
import com.tanfra.shopmob.features.common.dispatcher.DispatcherProvider
import com.tanfra.shopmob.features.commonScaffold.presentation.model.ScaffoldAction
import com.tanfra.shopmob.features.commonScaffold.presentation.model.ScaffoldEvent
import com.tanfra.shopmob.features.commonScaffold.presentation.model.ScaffoldMutation
import com.tanfra.shopmob.features.commonScaffold.presentation.view.ScaffoldViewState
import kotlinx.coroutines.flow.Flow

class ScaffoldViewModelMvi(
    actionProcessors: Collection<ActionProcessor<ScaffoldAction, ScaffoldMutation, ScaffoldEvent>>,
    reducers: Collection<Reducer<ScaffoldMutation, ScaffoldViewState>>,
    dispatcherProvider: DispatcherProvider,
    initialState: ScaffoldViewState = ScaffoldViewState(),
) : ViewModel() {
    private val model by model(actionProcessors, reducers, dispatcherProvider, initialState)

    internal val viewStateFlow: Flow<ScaffoldViewState> get() = model.viewStateFlow
    internal val eventFlow: Flow<ScaffoldEvent> get() = model.eventFlow

    fun process(action: ScaffoldAction) = model.process(action)
}