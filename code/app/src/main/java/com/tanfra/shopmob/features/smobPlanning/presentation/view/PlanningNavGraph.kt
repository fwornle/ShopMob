package com.tanfra.shopmob.features.smobPlanning.presentation.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.tanfra.shopmob.app.SmobApp
import com.tanfra.shopmob.features.smobPlanning.router.PlanningRoutes
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.ui.zeUtils.consolidateListItem

@Composable
fun PlanningNavGraph(
    navController: NavHostController = rememberNavController(),
) {

    NavHost(
        navController = navController,
        startDestination = "planningLists"
    ) {
        // all planning routes
        navigation(
            startDestination = PlanningRoutes.ListsBrowsingScreen.route,
            route = "planningLists"
        ) {

            composable(route = PlanningRoutes.ListsBrowsingScreen.route) {
                PlanningRoutes.ListsBrowsingScreen.Screen(
                    navController = navController,
                    onFilterList = { list -> onFilterList(list) },
                )
            }

            composable(route = PlanningRoutes.ListsAddItemScreen.route) {
                PlanningRoutes.ListsAddItemScreen.Screen(
                    navController = navController,
                )
            }

            composable(
                route = PlanningRoutes.SelectedListProductsScreen.route
                        + "?listId={listId}"
                        + "?listName={listName}",
                arguments = listOf(
                    navArgument("listId") { type = NavType.StringType },
                    navArgument("listName") { type = NavType.StringType },
                ),
            ) { backStackEntry ->
                PlanningRoutes.SelectedListProductsScreen.Screen(
                    navController = navController,
                    listId = backStackEntry.arguments?.getString("listId") ?: "unknown list id",
                    listName = backStackEntry.arguments?.getString("listName") ?: "ShopMob",
                    onShowProductDetails = { product ->
                        navController.navigate(
                            PlanningRoutes.SelectedListProductsScreen.route
                                    + "?productId=${product.id}"
                        )
                    },
                )
            }

            composable(route = PlanningRoutes.Screen3Screen.route) {
                PlanningRoutes.Screen3Screen.Screen()
            }

        }
    }
}


// mechanism to filter out list items
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
