package com.tanfra.shopmob.features.commonScaffold.router

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.tanfra.shopmob.R
import com.tanfra.shopmob.features.common.view.FabAddNewItem
import com.tanfra.shopmob.features.commonScaffold.presentation.view.TopLevelDestination
import com.tanfra.shopmob.features.smobPlanning.router.AdminRoutes
import com.tanfra.shopmob.features.smobPlanning.router.PlanningRoutes
import com.tanfra.shopmob.features.smobPlanning.router.ShopsRoutes
import timber.log.Timber

sealed class ScaffoldRoutes {

    // Application screens (TopLevel destinations)
    data object ScaffoldScreen : ScaffoldRoutes() {
        const val route = "app"
        const val title = "App"

        // initialize app TopLevel destinations (BottomBar and NavDrawer)
        private val topLevelDestinations = mutableListOf(
            TopLevelDestination(
                route = PlanningRoutes.ListsBrowseScreen.route,
                navTo = { /* completed by callback 'getTopLevelDestinations' */ },
                selectedIcon = R.drawable.list,
                unselectedIcon = R.drawable.ic_baseline_view_list_24,
                iconName = PlanningRoutes.ListsBrowseScreen.title,
                isBottomBar = true,
                isNavDrawer = true,
                title = PlanningRoutes.ListsBrowseScreen.title,
                goBackFlag = false,
                fab = null
            ),
            TopLevelDestination(
                route = AdminRoutes.AdminBrowseScreen.route,
                navTo = { /* completed by callback 'getTopLevelDestinations' */ },
                selectedIcon = R.drawable.ic_baseline_group_24,
                unselectedIcon = R.drawable.ic_baseline_group_24,
                iconName = AdminRoutes.AdminBrowseScreen.title,
                isBottomBar = false,
                isNavDrawer = true,
                title = AdminRoutes.AdminBrowseScreen.title,
                goBackFlag = false,
                fab = null
            ),
            TopLevelDestination(
                route = ShopsRoutes.ShopsBrowseScreen.route,
                navTo = { /* completed by callback 'getTopLevelDestinations' */ },
                selectedIcon = R.drawable.ic_baseline_shopping_cart_24,
                unselectedIcon = R.drawable.ic_baseline_shopping_cart_24,
                iconName = ShopsRoutes.ShopsBrowseScreen.title,
                isBottomBar = true,
                isNavDrawer = true,
                title = ShopsRoutes.ShopsBrowseScreen.title,
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
//                        saveState = true
                    }
//                    restoreState = true
                    launchSingleTop = true
                }
            }

            // configure all destinations
            topLevelDestinations[0] = topLevelDestinations[0].copy(
                navTo = { navToTopLevelDest(topLevelDestinations[0]) },
                fab = {
                    /* FAB: navigate to add item screen in order to add new list */
                    FabAddNewItem {
                        setNewScaffold(PlanningRoutes.ListsAddItemScreen.title, true, null)
                        navController.navigate(PlanningRoutes.ListsAddItemScreen.route)
                    }
                }
            )

            topLevelDestinations[1] = topLevelDestinations[1].copy(
                navTo = { navToTopLevelDest(topLevelDestinations[1]) }
            )

            topLevelDestinations[2] = topLevelDestinations[2].copy(
                navTo = { navToTopLevelDest(topLevelDestinations[2]) }
            )

            // return adjusted list of TopLevel destinations
            topLevelDestinations

        }

    }

}