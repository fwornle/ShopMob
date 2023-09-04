package com.tanfra.shopmob.smob.ui.details.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.details.SmobDetailsViewModel
import com.tanfra.shopmob.smob.ui.details.SmobDetailsViewState
import com.tanfra.shopmob.smob.ui.zeComponents.LoadingSpinner

@Composable
fun SmobItemDetailsScreen(viewModel: SmobDetailsViewModel) {

    // collect ui state flow
    val viewState: SmobDetailsViewState by viewModel.viewState.collectAsStateWithLifecycle()

    // content ready?
    if (viewState.isLoading) {
        LoadingSpinner()
    } else {
        SmobItemDetails(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimensionResource(R.dimen.horizontal_margin_medium)),
            item = viewState.item,
            cbMap = viewModel.sendToMap,  // used in SmobItemDetailsShop to send user to map
            cbShop = viewModel.sendToShop,  // used in SmobItemDetailsShop to send user to shop
        )
    }

}