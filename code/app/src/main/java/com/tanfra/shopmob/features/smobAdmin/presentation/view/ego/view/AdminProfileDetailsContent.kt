package com.tanfra.shopmob.features.smobAdmin.presentation.view.ego.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
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
import com.tanfra.shopmob.features.smobAdmin.presentation.view.AdminViewState
import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO
import timber.log.Timber

@Composable
fun AdminProfileDetailsContent(
    viewState: AdminViewState,
    onReload: () -> Unit = {},
    goBack: () -> Unit = {},
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
            AdminProfileDetailsView(
                user = viewState.selectedUser,
                goBack = goBack,
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
    name = "Shop Details",
    showSystemUi = true,
)
@Composable
fun PreviewAdminProfileDetailsContent() {

    val dE1 = SmobUserATO(status = ItemStatus.IN_PROGRESS)

    val viewState = AdminViewState(
        isConnectivityVisible = true,
        isLoaderVisible = false,
        isErrorVisible = false,
        errorMessage = "some error occured",
        isContentVisible = true,
        selectedUser = dE1
    )

    ShopMobTheme {
        AdminProfileDetailsContent(
            viewState = viewState,
            onReload = { Timber.i("Reload button pressed")}
        )
    }

}

