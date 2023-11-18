package com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.tanfra.shopmob.features.smobPlanning.presentation.PlanningViewModelMvi
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Action
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Event
import com.tanfra.shopmob.features.smobPlanning.presentation.view.ViewState
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PlanningListsAddItemScreen(
    viewModel: PlanningViewModelMvi,
    goBack: () -> Unit,
) {
    // lifecycle aware collection of viewState flow
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewState by viewModel.viewStateFlow
        .collectAsStateWithLifecycle(
            initialValue = ViewState(),
            lifecycleOwner = lifecycleOwner,
            minActiveState = Lifecycle.State.STARTED,
            context = viewModel.viewModelScope.coroutineContext,
        )

    // actions to be triggered (once) on CREATED
    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.process(action = Action.CheckConnectivity)
        }
    }

    // actions to be triggered (once) on STARTED
    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.process(action = Action.LoadGroups)

            // collect event flow - triggers reactions to signals from VM
            viewModel.eventFlow.collectLatest { event ->
                when (event) {
                    is Event.NavigateBack -> goBack()
                    else -> { /* ignore */ }
                }
            }
        }
    }


    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        PlanningListsAddItemContent(
            groupItems = viewState.groupItems.map { group -> Pair(group.id, group.name) },
            onSaveClicked = {
                    daName: String,
                    daDescription: String,
                    daSelection: Pair<String, String>,
                ->
                viewModel.process(
                    Action.SaveNewItem(daName, daDescription, daSelection)
                )
            },
        )
    }  // Scaffold

}
