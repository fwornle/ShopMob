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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.planning.PlanningViewModel
import com.tanfra.shopmob.smob.ui.planning.lists.PlanningListsUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber

@OptIn(
    ExperimentalCoroutinesApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun PlanningListsScreen(
    viewModel: PlanningViewModel,
) {

    // collect ui state flow
    val uiState: PlanningListsUiState by viewModel.uiStateListsSF.collectAsStateWithLifecycle(
        initialValue = PlanningListsUiState(isLoading = true),
    )

    // state of swipe refresh mechanism
    val isRefreshing by viewModel.isRefreshingSF.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullRefreshState(
        isRefreshing,
        viewModel::swipeRefreshListDataInLocalDB,
    )


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = { Timber.i("FAB pressed") }) {
                Icon(Icons.Filled.Add, stringResource(id = R.string.add_smob_item))
            }
        }
    ) { paddingValues ->

        Box(
            Modifier
                .pullRefresh(pullRefreshState)
        ) {

            Column(modifier = Modifier.padding(paddingValues)) {
                if (uiState.lists.isEmpty()) {
                    NoListsInfo()
                }
                else {
                    PlanningLists(
                        lists = uiState.lists,
                    )
                }
            }

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.align(Center))
                }
            }

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )

        }  // Box

    }  // Scaffold

}


@Composable
fun NoListsInfo() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
        Text(stringResource(R.string.no_data), color = Color.Gray)
    }
}
