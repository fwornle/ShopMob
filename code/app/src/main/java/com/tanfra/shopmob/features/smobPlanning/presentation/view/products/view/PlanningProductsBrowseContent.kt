package com.tanfra.shopmob.features.smobPlanning.presentation.view.products.view

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
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.features.common.theme.ShopMobTheme
import com.tanfra.shopmob.features.common.view.BannerView
import com.tanfra.shopmob.features.common.view.ErrorView
import com.tanfra.shopmob.features.smobPlanning.presentation.view.PlanningViewState
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import timber.log.Timber

@Composable
fun PlanningProductsBrowseContent(
    viewState: PlanningViewState,
    snackbarHostState: SnackbarHostState,
    onSwipeActionConfirmed: (SmobListATO, SmobProductATO) -> Unit,
    onSwipeIllegalTransition: () -> Unit,
    onClickItem: (SmobProductATO) -> Unit,
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
            PlanningProductsBrowseView(
                snackbarHostState = snackbarHostState,
                list = viewState.selectedList,
                products = viewState.productItemsOnList,
                onSwipeActionConfirmed = onSwipeActionConfirmed,
                onIllegalTransition = onSwipeIllegalTransition,
                onClickItem = onClickItem,
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
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
    name = "Planning Products",
    showSystemUi = true,
)
@Composable
fun PreviewPlanningProductsBrowseContent() {

    val dE1 = SmobProductATO(status = ItemStatus.IN_PROGRESS)
    val dE2 = SmobProductATO(status = ItemStatus.DONE)
    val daProducts = listOf(dE1, dE1, dE1, dE2, dE1, dE2, dE1, dE1, dE2, dE1, dE2, dE1)

    val viewState = PlanningViewState(
        isConnectivityVisible = true,
        isLoaderVisible = false,
        isErrorVisible = false,
        errorMessage = "some error occured",
        isContentVisible = true,
        productItemsOnList = daProducts
    )

    ShopMobTheme {
        PlanningProductsBrowseContent(
            viewState = viewState,
            snackbarHostState = SnackbarHostState(),
            onSwipeActionConfirmed = { _, _ -> },
            onSwipeIllegalTransition = {},
            onClickItem = {},
            onReload = { Timber.i("Reload button pressed")}
        )
    }

}

