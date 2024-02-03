package com.tanfra.shopmob.features.smobPlanning.router

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.tanfra.shopmob.R
import com.tanfra.shopmob.app.SmobApp
import com.tanfra.shopmob.features.common.view.FabAddNewItem
import com.tanfra.shopmob.features.commonScaffold.presentation.view.TopLevelDestination
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view.PlanningListsAddItemScreen
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view.PlanningListsBrowseScreen
import com.tanfra.shopmob.features.smobPlanning.presentation.view.products.view.PlanningProductDetailsScreen
import com.tanfra.shopmob.features.smobPlanning.presentation.view.products.view.PlanningProductsAddItemScreen
import com.tanfra.shopmob.features.smobPlanning.presentation.view.products.view.PlanningProductsBrowseScreen
import com.tanfra.shopmob.features.smobPlanning.presentation.view.shops.view.PlanningShopDetailsScreen
import com.tanfra.shopmob.features.smobPlanning.presentation.view.shops.view.PlanningShopsBrowseScreen
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.ui.zeUtils.consolidateListItem
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

sealed class PlanningRoutes {

    // planning screens cluster (nested NavGraph)
    data object PlanningScreens : PlanningRoutes() {
        const val route = "planningRoutes"
        const val title = "ShopMob"

        // initialize planning BottomBar destinations
        private val topLevelDestinations = mutableListOf(
                TopLevelDestination(
                    route = ListsBrowseScreen.route,
                    navTo = { /* BBarNav - completed by callback 'getBottomBarDestinations' */ },
                    selectedIcon = R.drawable.list,
                    unselectedIcon = R.drawable.ic_baseline_view_list_24,
                    iconName = ListsBrowseScreen.title,
                    isBottomBar = true,
                    isNavDrawer = true,
                    title = ListsBrowseScreen.title,
                    goBackFlag = false,
                    fab = null
                ),
                TopLevelDestination(
                    route = "Administration",
                    navTo = { /* drawerNav - completed by callback 'getDrawerMenuDestinations' */ },
                    selectedIcon = R.drawable.ic_baseline_group_24,
                    unselectedIcon = R.drawable.ic_baseline_group_24,
                    iconName = "Administration",
                    isBottomBar = false,
                    isNavDrawer = true,
                    title = "Administration",
                    goBackFlag = false,
                    fab = null
                ),
                TopLevelDestination(
                    route = ListsAddItemScreen.route,
                    navTo = { /* BBarNav - completed by callback 'getBottomBarDestinations' */ },
                    selectedIcon = R.drawable.ic_add,
                    unselectedIcon = R.drawable.ic_add,
                    iconName = ListsAddItemScreen.title,
                    isBottomBar = true,
                    isNavDrawer = false,
                    title = ListsAddItemScreen.title,
                    goBackFlag = false,
                    fab = null
                ),
                TopLevelDestination(
                    route = ShopsBrowseScreen.route,
                    navTo = { /* BBarNav - completed by callback 'getBottomBarDestinations' */ },
                    selectedIcon = R.drawable.ic_baseline_shopping_cart_24,
                    unselectedIcon = R.drawable.ic_baseline_shopping_cart_24,
                    iconName = ShopsBrowseScreen.title,
                    isBottomBar = true,
                    isNavDrawer = true,
                    title = ShopsBrowseScreen.title,
                    goBackFlag = false,
                    fab = null
                )
            )

        // getter function to return TopLevel navigation destinations while adjusting FAB entries
        // the latter typically perform "navigation actions" --> need navController --> lambda
        val getTopLevelDestinations = {
            navController: NavHostController,
            resetToScaffold: (String, Boolean, (@Composable () -> Unit)?) -> Unit,
            setNewScaffold: (String, Boolean, (@Composable () -> Unit)?) -> Unit
            ->

            // define lambda for navigation to top leve destination
            val navToTopLevelDest = { dest: TopLevelDestination ->
                // top level navigation --> reset scaffold stack
                resetToScaffold(
                    dest.title,
                    dest.goBackFlag,
                    dest.fab
                )
                Timber.i("MVI.UI: Triggering TopLevel navigation to ${dest.route}")
                // top level navigation --> reset popUp stack
                navController.navigate(dest.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    restoreState = true
                    launchSingleTop = true
                }
            }

            // configure all destinations
            topLevelDestinations[0] = topLevelDestinations[0].copy(
                navTo = { navToTopLevelDest(topLevelDestinations[0]) },
                fab = {
                    /* FAB: navigate to add item screen in order to add new list */
                    FabAddNewItem {
                        setNewScaffold(ListsAddItemScreen.title, true, null)
                        navController.navigate(ListsAddItemScreen.route)
                    }
                }
            )

            topLevelDestinations[1] = topLevelDestinations[1].copy(
                navTo = { navToTopLevelDest(topLevelDestinations[1]) }
            )

            topLevelDestinations[2] = topLevelDestinations[2].copy(
                navTo = { navToTopLevelDest(topLevelDestinations[2]) }
            )

            topLevelDestinations[3] = topLevelDestinations[3].copy(
                navTo = { navToTopLevelDest(topLevelDestinations[3]) }
            )

            // return adjusted list of TopLevel destinations
            topLevelDestinations

        }

    }

    // planning screens cluster (nested NavGraph)
    data object PlanningStart : PlanningRoutes() {
        const val route = "planningStart"
        const val title = "ShopMob Planning"
    }

    data object ListsBrowseScreen : PlanningRoutes() {
        const val route = "planningListsBrowsing"
        const val title = "Planning Lists"

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
            navigateToList: (list: SmobListATO) -> Unit,
        ) = PlanningListsBrowseScreen(
            viewModel = koinViewModel(),
            onNavigateToList = navigateToList,
        ) { list -> onFilterList(list) }
    }

    data object ListsAddItemScreen : PlanningRoutes() {
        const val route = "planningListsAddItem"
        const val title = "Add New List"

        @Composable
        fun Screen(
            setFab: ((@Composable () -> Unit)?) -> Unit,
            goBack: () -> Unit,
        ) = PlanningListsAddItemScreen(
            viewModel = koinViewModel(),
            setFab = setFab,
            goBack = goBack,
        )
    }

    data object ProductsBrowseScreen : PlanningRoutes() {
        const val route = "planningProductsBrowsing"
        const val title = "ShopMob Products"

        @Composable
        fun Screen(
            listId: String,
            navigateToProductDetails: (SmobProductATO) -> Unit,
        ) = PlanningProductsBrowseScreen(
            viewModel = koinViewModel(),
            listId = listId,
            onNavigateToProductDetails = navigateToProductDetails,
        )
    }

    data object ProductsAddItemScreen : PlanningRoutes() {
        const val route = "planningProductsAddItem"
        const val title = "Add New Product"

        @Composable
        fun Screen(
            selectedListId: String,
            navigateToShopSelect: () -> Unit,
            goBack: () -> Unit,
        ) = PlanningProductsAddItemScreen(
            viewModel = koinViewModel(),
            selectedListId = selectedListId,
            navigateToShopSelect = navigateToShopSelect,
            goBack = goBack,
        )
    }

    data object ProductDetailsScreen : PlanningRoutes() {
        const val route = "planningProductDetails"
        const val title = "Product Details"

        @Composable
        fun Screen(
            productId: String,
        ) = PlanningProductDetailsScreen(
            viewModel = koinViewModel(),
            productId = productId,
        )
    }

    data object ShopsBrowseScreen : PlanningRoutes() {
        const val route = "planningShopsBrowsing"
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

    data object ShopDetailsScreen : PlanningRoutes() {
        const val route = "planningShopDetails"
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