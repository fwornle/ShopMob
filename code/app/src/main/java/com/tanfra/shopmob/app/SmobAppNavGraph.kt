package com.tanfra.shopmob.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.tanfra.shopmob.app.Constants.INVALID_ITEM_ID
import com.tanfra.shopmob.features.common.view.FabAddNewItem
import com.tanfra.shopmob.features.common.view.FabSaveNewItem
import com.tanfra.shopmob.features.smobPlanning.router.PlanningRoutes
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO

fun NavGraphBuilder.routes(
    navController: NavHostController,
    setNewTitle: (String) -> Unit = {},
    restorePreviousTitle: () -> Unit = {},
    setGoBackFlag: (Boolean) -> Unit = {},
    setFab: ((@Composable () -> Unit)?) -> Unit = {},
) {
    // all planning routes
    navigation(
        startDestination = PlanningRoutes.ListsBrowseScreen.route,
        route = PlanningRoutes.PlanningScreens.route
    ) {

        with(PlanningRoutes.ListsBrowseScreen) {
            composable(route) {
                setNewTitle(title)
                setGoBackFlag(false)
                setFab {
                    FabAddNewItem {
                        /* onFab: navigate to add item screen in order to add new list */
                        navController.navigate(PlanningRoutes.ListsAddItemScreen.route)
                    }
                }
                Screen {
                    /* navigateToList */
                        list: SmobListATO ->
                    setNewTitle(list.name)
                    setFab {
                        FabAddNewItem {
                            /* onFab: navigate to add item screen in order to add new product */
                            navController.navigate(PlanningRoutes.ProductsAddItemScreen.route)
                        }
                    }
                    navController.navigate(
                        PlanningRoutes.ProductsBrowseScreen.route
                                + "/${list.id}?${list.name}"
                    )
                }
            }
        }

        with(PlanningRoutes.ListsAddItemScreen) {
            composable(route) {
                setNewTitle(title)
                setGoBackFlag(true)
                setFab(null)  // set inside screen logic, when "save" conditions have been met
                Screen(setFab) {
                    /* goBack */
                    restorePreviousTitle()
                    navController.popBackStack()
                }
            }
        }

        with(PlanningRoutes.ProductsBrowseScreen) {
            composable(
                route = "$route/{listId}?{listName}",
                arguments = listOf(
                    navArgument("listId") { type = NavType.StringType },
                    navArgument("listName") { type = NavType.StringType }
                ),
            ) { backStackEntry ->
                setNewTitle(backStackEntry.arguments?.getString("listName") ?: title)
                setGoBackFlag(true)
                setFab {
                    FabAddNewItem {
                        /* onFab: navigate to add item screen in order to add new product */
                        navController.navigate(PlanningRoutes.ProductsAddItemScreen.route)
                    }
                }
                Screen(
                    listId = backStackEntry.arguments
                        ?.getString("listId") ?: INVALID_ITEM_ID,
                ) { product ->
                    /* navigateToProductDetails */
                    setNewTitle(product.name)
                    setGoBackFlag(true)
                    setFab(null)
                    navController.navigate(
                        PlanningRoutes.ProductDetailsScreen.route
                                + "/${product.id}?${product.name}"
                    )
                }
            }
        }


        with(PlanningRoutes.ProductsAddItemScreen) {
           composable(route) {
               setNewTitle(title)
               setGoBackFlag(true)
               setFab {
                   FabSaveNewItem {
                       /* onFab: TODO: save and return  */
                       setFab(null)
                   }
               }
               Screen(
                   navigateToShopSelect= {
                       navController.navigate(PlanningRoutes.ShopsBrowseScreen.route) },
                   goBack = {
                       restorePreviousTitle()
                       navController.popBackStack()
                   }
               )
            }
        }

        with(PlanningRoutes.ProductDetailsScreen) {
            composable(
                route = "$route/{productId}?{productName}",
                arguments = listOf(
                    navArgument("productId") { type = NavType.StringType },
                    navArgument("productName") { type = NavType.StringType }
                ),
            ) { backStackEntry ->
                setNewTitle(backStackEntry.arguments?.getString("productName") ?: title)
                setGoBackFlag(true)
                setFab(null)
                Screen(
                    productId = backStackEntry.arguments
                        ?.getString("productId") ?: INVALID_ITEM_ID,
                )
            }
        }

        with (PlanningRoutes.ShopsBrowseScreen) {
            composable(route) {
                setNewTitle(title)
                setGoBackFlag(true)
                setFab(null)
                Screen { /* TODO: navigateToShopDetails */ }
            }
        }

        composable(route = PlanningRoutes.Screen3Screen.route) {
            PlanningRoutes.Screen3Screen.Screen()
        }

    }
}