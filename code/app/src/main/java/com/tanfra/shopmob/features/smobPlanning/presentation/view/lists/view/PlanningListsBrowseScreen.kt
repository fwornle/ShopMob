package com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import kotlinx.coroutines.flow.collectLatest

@OptIn(
    ExperimentalMaterialApi::class,
)
@Composable
fun PlanningListsBrowseScreen(
    viewModel: PlanningViewModelMvi,
    bottomBarDestinations: List<TopLevelDestination>,
    drawerMenuItems: List<Pair<ImageVector, String>>,
    navController: NavHostController,
    onFilterList: (List<SmobListATO>) -> List<SmobListATO>,
    onSwipeIllegalTransition: () -> Unit,
    onClickItem: (SmobListATO) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewState by viewModel.viewStateFlow
        .collectAsStateWithLifecycle(
            initialValue = ViewState(),
            lifecycleOwner = lifecycleOwner,
            minActiveState = Lifecycle.State.STARTED,
            context = viewModel.viewModelScope.coroutineContext,
        )

    // state of swipe refresh mechanism
    val reloadLists = { viewModel.process(Action.ReloadLists) }
    val isRefreshing by viewModel.isRefreshingSF.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullRefreshState(isRefreshing, reloadLists)

    // state of snackbar host
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.process(action = Action.CheckConnectivity)
        }
    }

    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.process(action = Action.LoadLists)

            viewModel.eventFlow.collectLatest { event ->
                when (event) {
                    // TODO
                    // TODO
                    // TODO
                    // TODO
                    // TODO
                    // TODO
                    // TODO
                    // TODO
                    // TODO
                    is Event.Refreshing -> { /* TODO */ }  // ???
                    is Event.GroupsLoaded -> { /* TODO */ }  // ???
                    // further events...
                }
            }
        }
    }

    ScreenScaffold(
        modifier = Modifier.fillMaxSize(),
        title = stringResource(id = R.string.app_name),
        bottomBarDestinations = bottomBarDestinations,
        drawerMenuItems = drawerMenuItems,
        navController = navController,  // needed in Scaffold for (dyn.) BottomBar navigation
    ) { paddingValues ->

        // Scaffold content
        Box(
            Modifier
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
                .fillMaxSize()
        ) {

            PlanningListsBrowseContent(
                modifier = Modifier,
                viewState = viewState,
                snackbarHostState = snackbarHostState,
                preFilteredItems = onFilterList(viewState.listItems),
                onSwipeActionConfirmed = { item: SmobListATO ->
                    viewModel.process(Action.ConfirmSwipe(item)) },
                onSwipeIllegalTransition = onSwipeIllegalTransition,
                onClickItem = onClickItem,
                onReload = reloadLists,
            )

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)  // 60 to move it above FABs
            )

        }  // Box

    }  // Scaffold content

}
