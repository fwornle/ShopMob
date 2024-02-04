package com.tanfra.shopmob.features.smobAdmin.router

import androidx.compose.runtime.Composable
import com.tanfra.shopmob.features.smobAdmin.presentation.view.AdminBrowseScreen

sealed class AdminRoutes {

    // cluster route for 'shops' (nested navigation graph)
    data object AdminScreens : AdminRoutes() {
        const val route = "adminScreens"
        const val title = "Admin Routes"
    }

    // (dummy) screen to initiate navigation logic - NavController just goes through
    data object AdminStart : AdminRoutes() {
        const val route = "adminStart"
        const val title = "Administration"
    }

    data object AdminBrowseScreen : AdminRoutes() {
        const val route = "adminBrowsing"
        const val title = "ShopMob Administration"

        @Composable
        fun Screen() = AdminBrowseScreen()
    }

}