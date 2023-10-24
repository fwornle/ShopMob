package com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.features.common.theme.ShopMobTheme
import com.tanfra.shopmob.features.common.view.BannerView
import com.tanfra.shopmob.features.common.view.ErrorView
import com.tanfra.shopmob.features.smobPlanning.presentation.view.ViewState
import timber.log.Timber

@Composable
fun PlanningListsBrowseContent(
    modifier: Modifier,
    viewState: ViewState,
    snackbarHostState: SnackbarHostState,
    preFilteredItems: List<SmobListATO>,
    onSwipeActionConfirmed: (SmobListATO) -> Unit,
    onSwipeIllegalTransition: () -> Unit,
    onClickItem: (SmobListATO) -> Unit,
    onReload: () -> Unit,
) {

    Column {

        if (viewState.isConnectivityVisible) {
            BannerView(
                modifier = Modifier
                    .padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = 8.dp,
                        bottom = 8.dp,
                    )
            )
        }

        if (viewState.isContentVisible) {
            PlanningListsBrowseView(
                snackbarHostState = snackbarHostState,
                lists = preFilteredItems,
                onSwipeActionConfirmed = onSwipeActionConfirmed,
                onIllegalTransition = onSwipeIllegalTransition,
                onClickItem = onClickItem,
            )
        }

        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {

            if (viewState.isLoaderVisible) {
                CircularProgressIndicator(
                    color = Color.Black,
                )
            }

            if (viewState.isErrorVisible) {
                ErrorView(
                    message = viewState.errorMessage,
                    buttonText = stringResource(id = R.string.smob_lists_action_reload),
                    onReload = onReload,
                )
            }
        }

    }

}


@Preview(
    name = "Planning Lists",
    showSystemUi = true,
)
@Composable
fun PreviewPlanningListsBrowseContent() {

    val dE1 = SmobListATO(status = ItemStatus.IN_PROGRESS)
    val dE2 = SmobListATO(status = ItemStatus.DONE)
    val daList = listOf(dE1, dE1, dE1, dE2, dE1, dE2, dE1, dE1, dE2, dE1, dE2, dE1)

    val viewState = ViewState(
        isConnectivityVisible = true,
        isLoaderVisible = false,
        isErrorVisible = false,
        errorMessage = "some error occured",
        isContentVisible = true,
        listItems = daList
    )

    ShopMobTheme {
        PlanningListsBrowseContent(
            modifier = Modifier,
            viewState = viewState,
            snackbarHostState = SnackbarHostState(),
            preFilteredItems = daList,
            onSwipeActionConfirmed = {},
            onSwipeIllegalTransition = {},
            onClickItem = {},
            onReload = { Timber.i("Reload button pressed")}
        )
    }

}

