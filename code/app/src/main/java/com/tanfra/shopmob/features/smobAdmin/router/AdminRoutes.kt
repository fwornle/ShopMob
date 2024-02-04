package com.tanfra.shopmob.features.smobAdmin.router

import androidx.compose.runtime.Composable
import com.tanfra.shopmob.features.smobAdmin.presentation.view.browse.AdminBrowseScreen
import com.tanfra.shopmob.features.smobAdmin.presentation.view.ego.view.AdminProfileDetailsScreen
import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO
import org.koin.androidx.compose.koinViewModel

sealed class AdminRoutes {

    // cluster route for 'shops' (nested navigation graph)
    data object AdminScreens : AdminRoutes() {
        const val route = "adminScreens"
        const val title = "Admin Routes"
    }

    // (dummy) screen to initiate navigation logic - NavController just goes through
    data object AdminStart : AdminRoutes() {
        const val route = "adminStart"
        const val title = "ShopMob Admin"
    }

    data object AdminBrowseScreen : AdminRoutes() {
        const val route = "adminBrowsing"
        const val title = "Administration"

        @Composable
        fun Screen(
            navigateToUserDetails: () -> Unit,
        ) = AdminBrowseScreen(
            navigateToUserDetails = navigateToUserDetails,
        )
    }

    data object AdminProfileDetailsScreen : AdminRoutes() {
        const val route = "profileDetails"
        const val title = "Profile Details"

        @Composable
        fun Screen(
            user: SmobUserATO,
            goBack: () -> Unit,
        ) = AdminProfileDetailsScreen(
            viewModel = koinViewModel(),
            user = user,
            goBack = goBack,
        )
    }

}