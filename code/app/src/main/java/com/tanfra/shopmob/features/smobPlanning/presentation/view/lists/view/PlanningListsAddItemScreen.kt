package com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.tanfra.shopmob.R
import com.tanfra.shopmob.features.common.view.ScreenScaffold
import com.tanfra.shopmob.features.common.view.TopLevelDestination
import com.tanfra.shopmob.features.smobPlanning.presentation.PlanningViewModelMvi
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Action
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Event
import com.tanfra.shopmob.features.smobPlanning.presentation.view.ViewState
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PlanningListsAddItemScreen(
    viewModel: PlanningViewModelMvi,
    navController: NavHostController,
    bottomBarDestinations: List<TopLevelDestination>,
    drawerMenuItems: List<Pair<ImageVector, String>>,
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
                    is Event.NavigateBack -> navController.popBackStack()
                    else -> { /* ignore */ }
                }
            }
        }
    }

    ScreenScaffold(
        title = stringResource(id = R.string.add_smob_item),
        canGoBack = true,
        bottomBarDestinations = bottomBarDestinations,
        drawerMenuItems = drawerMenuItems,
        navController = navController,
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
