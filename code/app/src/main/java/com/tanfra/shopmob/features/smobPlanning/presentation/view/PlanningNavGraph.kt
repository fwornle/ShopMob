package com.tanfra.shopmob.features.smobPlanning.presentation.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.tanfra.shopmob.features.smobPlanning.router.PlanningRoutes

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
                PlanningRoutes.ListsBrowsingScreen.Screen(navController)
            }

            composable(route = PlanningRoutes.ListsAddItemScreen.route) {
                PlanningRoutes.ListsAddItemScreen.Screen(navController)
            }

            composable(
                route = PlanningRoutes.SelectedListProductsBrowseScreen.route
                        + "/{listId}"
                        + "?listName={listName}",
                arguments = listOf(
                    navArgument("listId") { type = NavType.StringType },
                    navArgument("listName") { type = NavType.StringType },
                ),
            ) { backStackEntry ->
                PlanningRoutes.SelectedListProductsBrowseScreen.Screen(
                    navController = navController,
                    listId = backStackEntry.arguments?.getString("listId") ?: "unknown list id",
                    listName = backStackEntry.arguments?.getString("listName") ?: "ShopMob",
                )
            }

            composable(
                route = PlanningRoutes.SelectedProductDetailsScreen.route
                        + "/{productId}"
                        + "?productName={productName}",
                arguments = listOf(
                    navArgument("productId") { type = NavType.StringType },
                    navArgument("productName") { type = NavType.StringType },
                ),
            ) { backStackEntry ->
                PlanningRoutes.SelectedProductDetailsScreen.Screen(
                    navController = navController,
                    productId = backStackEntry.arguments?.getString("productId") ?: "unknown product id",
                    productName = backStackEntry.arguments?.getString("productName") ?: "Mystery",
                )
            }

            composable(route = PlanningRoutes.Screen3Screen.route) {
                PlanningRoutes.Screen3Screen.Screen()
            }

        }
    }
}
