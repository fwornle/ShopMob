package com.tanfra.shopmob.smob.ui.planning.lists.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tanfra.shopmob.smob.ui.planning.PlanningViewModel
import com.tanfra.shopmob.smob.ui.planning.lists.PlanningListsUiState
import com.tanfra.shopmob.smob.ui.zeComponents.NoListItemsInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(
    ExperimentalCoroutinesApi::class,
    ExperimentalMaterialApi::class,
)
@Composable
fun PlanningListsScreen(
    viewModel: PlanningViewModel,
//    paddingValues: PaddingValues,
) {

    // collect ui state flow
    val uiState: PlanningListsUiState by viewModel.uiStateLists.collectAsStateWithLifecycle(
        initialValue = PlanningListsUiState(isLoaderVisible = true),
    )

    // state of swipe refresh mechanism4
    val isRefreshing by viewModel.isRefreshingSF.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullRefreshState(
        isRefreshing,
        viewModel::swipeRefreshListDataInLocalDB,
    )

    // state of snackbar host
    val snackbarHostState = remember { SnackbarHostState() }

    Box(
        Modifier
            .pullRefresh(pullRefreshState)
            .fillMaxSize()
    ) {

        // the actual list
        Column(modifier = Modifier
//            .padding(paddingValues)
        ) {
            if (uiState.lists.isEmpty()) {
                NoListItemsInfo()
            } else {
                PlanningLists(
                    lists = uiState.lists,
                    listFilter = viewModel::listFilter,
                    snackbarHostState = snackbarHostState,
                    onSwipeActionConfirmed = viewModel::swipeActionConfirmed,
                    onIllegalTransition = viewModel::onIllegalTransition,
                    onClick = viewModel::sendToList,
                )
            }
        }

        // or a loader
        if (uiState.isLoaderVisible) {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Center))
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)  // move above FABs
        )

    }  // Box

}
