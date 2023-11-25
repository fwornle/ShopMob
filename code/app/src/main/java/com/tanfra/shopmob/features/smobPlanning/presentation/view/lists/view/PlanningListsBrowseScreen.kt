package com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.tanfra.shopmob.features.smobPlanning.presentation.PlanningViewModelMvi
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningAction
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningEvent
import com.tanfra.shopmob.features.smobPlanning.presentation.view.PlanningViewState
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import kotlinx.coroutines.flow.collectLatest

@OptIn(
    ExperimentalMaterialApi::class,
)
@Composable
fun PlanningListsBrowseScreen(
    viewModel: PlanningViewModelMvi,
    onNavigateToList: (SmobListATO) -> Unit,
    onFilterList: (List<SmobListATO>) -> List<SmobListATO>,
) {
    // lifecycle aware collection of viewState flow
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewState by viewModel.viewStateFlow
        .collectAsStateWithLifecycle(
            initialValue = PlanningViewState(),
            lifecycleOwner = lifecycleOwner,
            minActiveState = Lifecycle.State.STARTED,
            context = viewModel.viewModelScope.coroutineContext,
        )

    // local state of pull refresh mechanism (incl. associated refresh action)
    val reloadLists = { viewModel.process(PlanningAction.RefreshLists) }
    val pullRefreshState = rememberPullRefreshState(viewState.isRefreshing, reloadLists)

//    // state of snackbar host
//    val snackbarHostState = remember { SnackbarHostState() }

    // actions to be triggered (once) on CREATED
    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.process(action = PlanningAction.CheckConnectivity)
        }
    }

    // actions to be triggered (once) on STARTED
    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.process(action = PlanningAction.LoadLists)

            // collect event flow - triggers reactions to signals from VM
            viewModel.eventFlow.collectLatest { event ->
                when (event) {
                    is PlanningEvent.NavigateToList -> onNavigateToList(event.list)
                    else -> { /* ignore */ }
                    // further events...
                }
            }
        }
    }


    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // Scaffold content
        Box(
            Modifier.pullRefresh(pullRefreshState)
        ) {

            PlanningListsBrowseContent(
                viewState = viewState,
                preFilteredItems = onFilterList(viewState.listItems),
                onSwipeActionConfirmed = { item: SmobListATO ->
                    viewModel.process(PlanningAction.ConfirmListSwipe(item)) },
                onSwipeIllegalTransition = { viewModel.process(PlanningAction.IllegalSwipe) },
                onClickItem = { list: SmobListATO ->
                    viewModel.process(PlanningAction.NavigateToProductsOnList(list)) },
                onReload = reloadLists,
            )

            PullRefreshIndicator(
                refreshing = viewState.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            SnackbarHost(
                hostState = viewState.snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)  // 60 to move it above FABs
            )

        }  // Box

    }  // Scaffold content

}
