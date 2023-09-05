package com.tanfra.shopmob.smob.ui.details.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.details.SmobDetailsViewModel
import com.tanfra.shopmob.smob.ui.zeComponents.LoadingSpinner
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun DetailsScreen(viewModel: SmobDetailsViewModel) {

    // collect ui state flow
    with(viewModel.collectAsState().value) {

        // content ready?
        if (isLoading) {
            LoadingSpinner()
        } else {
            DetailsItem(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(R.dimen.horizontal_margin_medium)),
                item = item,              // SmobShopATO or SmobProductATO
                sendToMap = sendToMap,    // only used in SmobItemDetailsShop
                sendToShop = sendToShop,  // only used in SmobItemDetailsShop
            )
        }

    }

}