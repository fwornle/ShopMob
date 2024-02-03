package com.tanfra.shopmob.features.smobPlanning.router

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

sealed class AdminRoutes {

    // cluster route for 'shops' (nested navigation graph)
    data object AdminScreens : AdminRoutes() {
        const val route = "adminScreens"
        const val title = "Admin Routes"
    }

    // (dummy) screen to initiate navigation logic - NavController just goes through
    data object AdminStart : AdminRoutes() {
        const val route = "adminStart"
        const val title = "ShopMob Administration"
    }

    data object AdminBrowseScreen : AdminRoutes() {
        const val route = "adminBrowsing"
        const val title = "Administration"

        @Composable
        fun Screen() = Text("Administration... coming soon")
    }

}