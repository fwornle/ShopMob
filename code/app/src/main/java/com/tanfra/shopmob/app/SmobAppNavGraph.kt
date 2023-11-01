package com.tanfra.shopmob.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.tanfra.shopmob.features.smobPlanning.router.PlanningRoutes
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO

@Composable
fun SmobAppNavGraph(
    navController: NavHostController,
    onSetGoBackFlag: (Boolean) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = "planningRoutes"
    ) {
        // all planning routes
        navigation(
            startDestination = PlanningRoutes.ListsBrowsingScreen.route,
            route = "planningRoutes"
        ) {

            composable(route = PlanningRoutes.ListsBrowsingScreen.route) {
                PlanningRoutes.ListsBrowsingScreen.Screen(
                    onSetGoBackFlag = onSetGoBackFlag,
                ) { list: SmobListATO ->
                    navController.navigate(
                        PlanningRoutes.SelectedListProductsBrowseScreen.route
                                + "/${list.id}"
                                + "?listName=${list.name}"
                    )
                }
            }

            composable(route = PlanningRoutes.ListsAddItemScreen.route) {
                PlanningRoutes.ListsAddItemScreen.Screen(
                    goBack = { navController.popBackStack() }
                )
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
                    listId = backStackEntry.arguments?.getString("listId") ?: "unknown list id",
                    onSetGoBackFlag = onSetGoBackFlag
                ) { product ->
                    navController.navigate(
                        PlanningRoutes.SelectedProductDetailsScreen.route
                                + "/${product.id}"
                                + "?productName=${product.name}"
                    )
                }
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
                    productId = backStackEntry.arguments?.getString("productId") ?: "unknown product id",
                    productName = backStackEntry.arguments?.getString("productName") ?: "Mystery",
                    onSetGoBackFlag = onSetGoBackFlag,
                )
            }

            composable(route = PlanningRoutes.Screen3Screen.route) {
                PlanningRoutes.Screen3Screen.Screen()
            }

        }
    }
}
