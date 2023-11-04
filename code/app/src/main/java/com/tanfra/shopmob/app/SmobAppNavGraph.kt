package com.tanfra.shopmob.app

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.tanfra.shopmob.app.Constants.INVALID_SMOB_ITEM_ID
import com.tanfra.shopmob.features.smobPlanning.router.PlanningRoutes
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO

fun NavGraphBuilder.routes(
    navController: NavHostController,
    setTitle: (String) -> Unit,
    saveTitle: () -> Unit,
    restorePreviousTitle: () -> Unit,
    onSetGoBackFlag: (Boolean) -> Unit,
) {
    // all planning routes
    navigation(
        startDestination = PlanningRoutes.ListsBrowseScreen.route,
        route = PlanningRoutes.PlanningScreens.route
    ) {

        composable(route = PlanningRoutes.ListsBrowseScreen.route) {
            PlanningRoutes.ListsBrowseScreen.Screen(
                onSetGoBackFlag = onSetGoBackFlag,
            ) {
                /* navigateToList */
                list: SmobListATO ->
                saveTitle()
                setTitle(list.name)
                navController.navigate(
                    PlanningRoutes.ProductsBrowseScreen.route
                            + "/${list.id}"
                )
            }
        }

        composable(route = PlanningRoutes.ListsAddItemScreen.route) {
            PlanningRoutes.ListsAddItemScreen.Screen {
                /* goBack */
                restorePreviousTitle()
                navController.popBackStack()
            }
        }

        composable(
            route = PlanningRoutes.ProductsBrowseScreen.route
                    + "/{listId}",
            arguments = listOf(
                navArgument("listId") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            PlanningRoutes.ProductsBrowseScreen.Screen(
                listId = backStackEntry.arguments?.getString("listId") ?: INVALID_SMOB_ITEM_ID,
                onSetGoBackFlag = onSetGoBackFlag
            ) { product ->
                saveTitle()
                setTitle(product.name)
                navController.navigate(
                    PlanningRoutes.ProductDetailsScreen.route
                            + "/${product.id}"
                )
            }
        }

        composable(route = PlanningRoutes.ProductsAddItemScreen.route) {
            PlanningRoutes.ProductsAddItemScreen.Screen(
                navigateToShopSelect= {
                    navController.navigate(PlanningRoutes.ShopsBrowseScreen.route) },
                goBack = {
                    restorePreviousTitle()
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = PlanningRoutes.ProductDetailsScreen.route
                    + "/{productId}",
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            PlanningRoutes.ProductDetailsScreen.Screen(
                productId = backStackEntry.arguments?.getString("productId") ?: INVALID_SMOB_ITEM_ID,
                onSetGoBackFlag = onSetGoBackFlag,
            )
        }

        composable(route = PlanningRoutes.ShopsBrowseScreen.route) {
            PlanningRoutes.ShopsBrowseScreen.Screen(
                onSetGoBackFlag = onSetGoBackFlag,
            ) { /* TODO: navigateToShopDetails */ }
        }

        composable(route = PlanningRoutes.Screen3Screen.route) {
            PlanningRoutes.Screen3Screen.Screen()
        }

    }
}