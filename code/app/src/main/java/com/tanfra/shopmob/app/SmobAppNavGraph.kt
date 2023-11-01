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
    mainTitle: String,
    onSetTitle: (String) -> Unit,
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
                    onSetTitle(list.name)  // fw231101 - currently ineffective (state not reachable)
                    navController.navigate(
                        PlanningRoutes.SelectedListProductsBrowseScreen.route
                                + "/${list.id}"
                    )
                }
            }

            composable(route = PlanningRoutes.ListsAddItemScreen.route) {
                PlanningRoutes.ListsAddItemScreen.Screen(
                    goBack = {
                        onSetTitle(mainTitle)
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = PlanningRoutes.SelectedListProductsBrowseScreen.route
                        + "/{listId}",
                arguments = listOf(
                    navArgument("listId") { type = NavType.StringType },
                ),
            ) { backStackEntry ->
                PlanningRoutes.SelectedListProductsBrowseScreen.Screen(
                    listId = backStackEntry.arguments?.getString("listId") ?: "unknown list id",
                    onSetGoBackFlag = onSetGoBackFlag
                ) { product ->
                    onSetTitle(product.name)  // fw231101 - currently ineffective
                    navController.navigate(
                        PlanningRoutes.SelectedProductDetailsScreen.route
                                + "/${product.id}"
                    )
                }
            }

            composable(
                route = PlanningRoutes.SelectedProductDetailsScreen.route
                        + "/{productId}",
                arguments = listOf(
                    navArgument("productId") { type = NavType.StringType },
                ),
            ) { backStackEntry ->
                PlanningRoutes.SelectedProductDetailsScreen.Screen(
                    productId = backStackEntry.arguments?.getString("productId") ?: "unknown product id",
                    onSetGoBackFlag = onSetGoBackFlag,
                )
            }

            composable(route = PlanningRoutes.Screen3Screen.route) {
                PlanningRoutes.Screen3Screen.Screen()
            }

        }
    }
}
