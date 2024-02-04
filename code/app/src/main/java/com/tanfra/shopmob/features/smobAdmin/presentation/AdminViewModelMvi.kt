package com.tanfra.shopmob.features.smobAdmin.presentation

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import com.tanfra.shopmob.features.common.arch.ActionProcessor
import com.tanfra.shopmob.features.common.arch.Reducer
import com.tanfra.shopmob.features.common.arch.model
import com.tanfra.shopmob.features.common.dispatcher.DispatcherProvider
import com.tanfra.shopmob.features.smobAdmin.presentation.model.AdminAction
import com.tanfra.shopmob.features.smobAdmin.presentation.model.AdminEvent
import com.tanfra.shopmob.features.smobAdmin.presentation.model.AdminMutation
import com.tanfra.shopmob.features.smobAdmin.presentation.view.AdminViewState
import kotlinx.coroutines.flow.Flow

// VM declared STABLE, as VM consumers will be notified when any contents change (via observers)
@Stable
class AdminViewModelMvi(
    actionProcessors: Collection<ActionProcessor<AdminAction, AdminMutation, AdminEvent>>,
    reducers: Collection<Reducer<AdminMutation, AdminViewState>>,
    dispatcherProvider: DispatcherProvider,
    initialState: AdminViewState = AdminViewState(),
) : ViewModel() {
    private val model by model(actionProcessors, reducers, dispatcherProvider, initialState)

    internal val viewStateFlow: Flow<AdminViewState> get() = model.viewStateFlow
    internal val eventFlow: Flow<AdminEvent> get() = model.eventFlow

    fun process(action: AdminAction) = model.process(action)
}