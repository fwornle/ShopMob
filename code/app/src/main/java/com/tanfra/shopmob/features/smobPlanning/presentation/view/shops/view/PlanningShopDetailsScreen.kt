package com.tanfra.shopmob.features.smobPlanning.presentation.view.shops.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.tanfra.shopmob.features.smobPlanning.presentation.PlanningViewModelMvi
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningAction
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningEvent
import com.tanfra.shopmob.features.smobPlanning.presentation.view.PlanningViewState
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@OptIn(
    ExperimentalMaterialApi::class,
)
@Composable
fun PlanningShopDetailsScreen(
    viewModel: PlanningViewModelMvi,
    shopId: String,
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

    // state of swipe refresh mechanism
    val reloadShops = { viewModel.process(PlanningAction.RefreshProducts) }
    val pullRefreshState = rememberPullRefreshState(viewState.isRefreshing, reloadShops)


    // actions to be triggered (once) on STARTED
    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.process(action = PlanningAction.LoadShop(shopId))

            // collect event flow - triggers reactions to signals from VM
            viewModel.eventFlow.collectLatest { event ->
                when (event) {
                    is PlanningEvent.SampleEvent -> { /* sampleEventReaction here... */ }
                    else -> { Timber.i("Received unspecified PlanningEvent: $event") }
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

            PlanningShopDetailsContent(
                viewState = viewState,
                onReload = { /* TODO: define something useful here... */ }
            )

            PullRefreshIndicator(
                refreshing = viewState.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )

        }  // Box

    }  // Scaffold content

}
