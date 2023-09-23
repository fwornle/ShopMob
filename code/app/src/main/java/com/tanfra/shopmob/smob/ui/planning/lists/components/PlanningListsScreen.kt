package com.tanfra.shopmob.smob.ui.planning.lists.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.planning.PlanningViewModel
import com.tanfra.shopmob.smob.ui.planning.lists.PlanningListsUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(
    ExperimentalCoroutinesApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun PlanningListsScreen(
    viewModel: PlanningViewModel,
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


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton( onClick = viewModel::navigateToAddSmobList ) {
                Icon(Icons.Filled.Add, stringResource(id = R.string.add_smob_item))
            }
        }
    ) { paddingValues ->

        Box(
            Modifier
                .pullRefresh(pullRefreshState)
                .fillMaxSize()
        ) {

            Column(modifier = Modifier.padding(paddingValues)) {
                if (uiState.lists.isEmpty()) {
                    NoListsInfo()
                }
                else {
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

    }  // Scaffold

}

@Composable
fun NoListsInfo() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
        Icon(
            painter = painterResource(id = R.drawable.ic_no_data),
            contentDescription = "No Data"
        )
        Text(
            text = stringResource(R.string.no_data),
            color = Color.Gray,
            fontSize = 16.sp,
        )
    }
}
