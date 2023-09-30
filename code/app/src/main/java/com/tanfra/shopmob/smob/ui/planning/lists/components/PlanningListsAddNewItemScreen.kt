package com.tanfra.shopmob.smob.ui.planning.lists.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tanfra.shopmob.smob.ui.planning.PlanningViewModel
import com.tanfra.shopmob.smob.ui.planning.lists.PlanningListsAddNewItemsUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(
    ExperimentalCoroutinesApi::class,
    ExperimentalMaterialApi::class,
)
@Composable
fun PlanningListsAddNewItemScreen(
    viewModel: PlanningViewModel,
    onNavigateBack: () -> Unit,
) {

    // collect ui state flow
    val uiState by viewModel.uiStateListsAddNewItem.collectAsStateWithLifecycle(
        initialValue = PlanningListsAddNewItemsUiState(),
    )

    // state of swipe refresh mechanism
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
        ) {
            Text("PlanningLists New Item")
//            PlanningLists(
//                lists = uiState.lists,
//                listFilter = viewModel::listFilter,
//                snackbarHostState = snackbarHostState,
//                onSwipeActionConfirmed = viewModel::swipeActionConfirmed,
//                onIllegalTransition = viewModel::onIllegalTransition,
//                onClick = viewModel::sendToList,
//            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)  // move above FABs
        )

    }  // Box

}
