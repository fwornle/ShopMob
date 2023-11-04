package com.tanfra.shopmob.features.smobPlanning.router

import androidx.compose.runtime.Composable
import com.tanfra.shopmob.app.SmobApp
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view.PlanningListsAddItemScreen
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view.PlanningListsBrowseScreen
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view.PlanningShopsBrowseScreen
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view.Screen3
import com.tanfra.shopmob.features.smobPlanning.presentation.view.products.view.PlanningProductDetailsScreen
import com.tanfra.shopmob.features.smobPlanning.presentation.view.products.view.PlanningProductsAddItemScreen
import com.tanfra.shopmob.features.smobPlanning.presentation.view.products.view.PlanningProductsBrowseScreen
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.ui.zeUtils.consolidateListItem
import org.koin.androidx.compose.koinViewModel

sealed class PlanningRoutes {

    // planning screens cluster (nested NavGraph)
    data object PlanningScreens : PlanningRoutes() {
        const val route = "planningRoutes"
        const val title = "ShopMob"
    }

    data object ListsBrowseScreen : PlanningRoutes() {
        const val route = "planningListsBrowsing"

        // mechanism to filter out SmobList items which belong to the current user
        private fun onFilterList(items: List<SmobListATO>): List<SmobListATO> {
            // take out all items which have been deleted by swiping
            return items
                .filter { item -> item.groups
                    .map { group -> group.id }
                    .intersect((SmobApp.currUser?.groups ?: listOf()).toSet())
                    .any()
                }
                .filter { item -> item.status != ItemStatus.DELETED  }
                .map { item -> consolidateListItem(item) }
                .sortedWith(
                    compareBy { it.position }
                )
        }

        @Composable
        fun Screen(
            onSetGoBackFlag: (Boolean) -> Unit,
            navigateToList: (list: SmobListATO) -> Unit,
        ) = PlanningListsBrowseScreen(
            viewModel = koinViewModel(),
            onSetGoBackFlag = onSetGoBackFlag,
            onNavigateToList = navigateToList,
        ) { list -> onFilterList(list) }
    }

    data object ListsAddItemScreen : PlanningRoutes() {
        const val route = "planningListsAddItem"

        @Composable
        fun Screen(
            goBack: () -> Unit,
        ) = PlanningListsAddItemScreen(
            viewModel = koinViewModel(),
            goBack = goBack,
        )
    }

    data object ProductsBrowseScreen : PlanningRoutes() {
        const val route = "planningProductsBrowsing"

        @Composable
        fun Screen(
            listId: String,
            onSetGoBackFlag: (Boolean) -> Unit,
            navigateToProductDetails: (SmobProductATO) -> Unit,
        ) = PlanningProductsBrowseScreen(
            viewModel = koinViewModel(),
            listId = listId,
            onSetGoBackFlag = onSetGoBackFlag,
            onNavigateToProductDetails = navigateToProductDetails,
        )
    }

    data object ProductsAddItemScreen : PlanningRoutes() {
        const val route = "planningProductsAddItem"

        @Composable
        fun Screen(
            navigateToShopSelect: () -> Unit,
            goBack: () -> Unit,
        ) = PlanningProductsAddItemScreen(
            viewModel = koinViewModel(),
            navigateToShopSelect = navigateToShopSelect,
            goBack = goBack,
        )
    }

    data object ProductDetailsScreen : PlanningRoutes() {
        const val route = "planningProductDetails"

        @Composable
        fun Screen(
            productId: String,
            onSetGoBackFlag: (Boolean) -> Unit,
        ) = PlanningProductDetailsScreen(
            viewModel = koinViewModel(),
            productId = productId,
            onSetGoBackFlag = onSetGoBackFlag,
        )
    }

    data object ShopsBrowseScreen : PlanningRoutes() {
        const val route = "planningShopsBrowsing"

        @Composable
        fun Screen(
            onSetGoBackFlag: (Boolean) -> Unit,
            navigateToShopDetails: (SmobShopATO) -> Unit,
        ) = PlanningShopsBrowseScreen(
            viewModel = koinViewModel(),
            onSetGoBackFlag = onSetGoBackFlag,
            onNavigateToShopsDetails = navigateToShopDetails,
        )
    }

    data object Screen3Screen : PlanningRoutes() {
        const val route = "planningListsSreen3"

        @Composable
        fun Screen() = Screen3()
    }

}