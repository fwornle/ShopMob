package com.tanfra.shopmob.features.smobPlanning.router

import androidx.compose.runtime.Composable
import com.tanfra.shopmob.features.smobPlanning.presentation.view.shops.view.PlanningShopDetailsScreen
import com.tanfra.shopmob.features.smobPlanning.presentation.view.shops.view.PlanningShopsBrowseScreen
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.types.ItemStatus
import org.koin.androidx.compose.koinViewModel

sealed class ShopsRoutes {

    // cluster route for 'shops' (nested navigation graph)
    data object ShopsScreens : ShopsRoutes() {
        const val route = "shopsScreens"
        const val title = "Shops Routes"
    }

    // (dummy) screen to initiate navigation logic - NavController just goes through
    data object ShopsStart : ShopsRoutes() {
        const val route = "shopsStart"
        const val title = "ShopMob Shops"
    }

    data object ShopsBrowseScreen : ShopsRoutes() {
        const val route = "shopsBrowsing"
        const val title = "ShopMob Shops"

        // mechanism to filter out SmobList items which belong to the current user
        private fun onFilterList(items: List<SmobShopATO>): List<SmobShopATO> {
            // take out all items which have been deleted by swiping
            return items
                .filter { item -> item.status != ItemStatus.DELETED  }
                .sortedWith(
                    compareBy { it.position }
                )
        }

        @Composable
        fun Screen(
            navigateToShopDetails: (SmobShopATO) -> Unit,
        ) = PlanningShopsBrowseScreen(
            viewModel = koinViewModel(),
            onNavigateToShop = navigateToShopDetails,
        ) { list -> onFilterList(list) }
    }

    data object ShopDetailsScreen : ShopsRoutes() {
        const val route = "shopDetails"
        const val title = "Shop Details"

        @Composable
        fun Screen(
            shopId: String,
        ) = PlanningShopDetailsScreen(
            viewModel = koinViewModel(),
            shopId = shopId,
        )
    }

}