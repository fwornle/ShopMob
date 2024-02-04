package com.tanfra.shopmob.features.smobAdmin.presentation.view.ego.view

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
import com.tanfra.shopmob.features.smobAdmin.presentation.AdminViewModelMvi
import com.tanfra.shopmob.features.smobAdmin.presentation.model.AdminAction
import com.tanfra.shopmob.features.smobAdmin.presentation.model.AdminEvent
import com.tanfra.shopmob.features.smobAdmin.presentation.view.AdminViewState
import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@OptIn(
    ExperimentalMaterialApi::class,
)
@Composable
fun AdminProfileDetailsScreen(
    viewModel: AdminViewModelMvi,
    user: SmobUserATO,
    goBack: () -> Unit,
) {
    // lifecycle aware collection of viewState flow
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewState by viewModel.viewStateFlow
        .collectAsStateWithLifecycle(
            initialValue = AdminViewState(),
            lifecycleOwner = lifecycleOwner,
            minActiveState = Lifecycle.State.STARTED,
            context = viewModel.viewModelScope.coroutineContext,
        )

    // state of swipe refresh mechanism
    val reloadShops = { viewModel.process(AdminAction.RefreshUsers) }
    val pullRefreshState = rememberPullRefreshState(viewState.isRefreshing, reloadShops)


    // actions to be triggered (once) on STARTED
    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            // re-load details of currently logged-in user and set as current user in viewState
            viewModel.process(action = AdminAction.SetCurrentUser(user))

            // collect event flow - triggers reactions to signals from VM
            viewModel.eventFlow.collectLatest { event ->
                when (event) {
                    is AdminEvent.SampleEvent -> { /* sampleEventReaction here... */ }
                    else -> { Timber.i("Received unspecified AdminEvent: $event") }
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

            AdminProfileDetailsContent(
                viewState = viewState,
                onReload = { /* TODO: define something useful here... */ },
                goBack = goBack,
            )

            PullRefreshIndicator(
                refreshing = viewState.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )

        }  // Box

    }  // Scaffold content

}
