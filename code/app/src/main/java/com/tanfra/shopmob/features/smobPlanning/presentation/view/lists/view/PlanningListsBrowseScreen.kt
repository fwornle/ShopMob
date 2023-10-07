package com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.tanfra.shopmob.R
import com.tanfra.shopmob.features.smobPlanning.presentation.PlanningViewModel
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.PlanningListsBrowseUiState
import com.tanfra.shopmob.features.common.view.NoListItemsInfo
import com.tanfra.shopmob.features.common.view.ScreenScaffold
import com.tanfra.shopmob.features.common.view.TopLevelDestination
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.compose.koinViewModel

@OptIn(
    ExperimentalCoroutinesApi::class,
    ExperimentalMaterialApi::class,
)
@Composable
fun PlanningListsBrowseScreen(
    bottomBarDestinations: List<TopLevelDestination>,
    drawerMenuItems: List<Pair<ImageVector, String>>,
    navController: NavHostController,  // can avoid passing NC when rev. order: Scaffold -> NavHost
) {
    // inject VM
    val viewModel: PlanningViewModel = koinViewModel()

    // collect ui state flow
    val uiState by viewModel.uiStateLists.collectAsStateWithLifecycle(
        initialValue = PlanningListsBrowseUiState(),
    )

    // state of swipe refresh mechanism4
    val isRefreshing by viewModel.isRefreshingSF.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullRefreshState(
        isRefreshing,
        viewModel::swipeRefreshListDataInLocalDB,
    )

    // state of snackbar host
    val snackbarHostState = remember { SnackbarHostState() }

    ScreenScaffold(
        modifier = Modifier.fillMaxSize(),
        title = stringResource(id = R.string.app_name),
        canGoBack = false,
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

            // the actual list
            Column(
                modifier = Modifier
            ) {

                // still loading?
                if (uiState.isLoaderVisible) {
                    // flow collection still ongoing
                    Box(Modifier.fillMaxSize()) {
                        CircularProgressIndicator(Modifier.align(Center))
                    }
                } else {
                    // flow collected - some result available
                    if (uiState.lists.isEmpty()) {
                        NoListItemsInfo()
                    } else {
                        PlanningListsBrowse(
                            lists = uiState.lists,
                            listFilter = viewModel::listFilter,
                            snackbarHostState = snackbarHostState,
                            onSwipeActionConfirmed = viewModel::swipeActionConfirmed,
                            onIllegalTransition = viewModel::onIllegalTransition,
                            onClick = viewModel::sendToList,
                        )
                    }
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
                    .padding(bottom = 8.dp)  // 60 to move it above FABs
            )

        }  // Box

    }  // Scaffold content

}
