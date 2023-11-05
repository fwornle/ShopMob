package com.tanfra.shopmob.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.tanfra.shopmob.app.Constants.INVALID_SMOB_ITEM_ID
import com.tanfra.shopmob.features.common.view.FabAddNewItem
import com.tanfra.shopmob.features.common.view.FabSaveNewItem
import com.tanfra.shopmob.features.smobPlanning.router.PlanningRoutes
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO

fun NavGraphBuilder.routes(
    navController: NavHostController,
    setTitle: (String) -> Unit = {},
    saveTitle: () -> Unit = {},
    restorePreviousTitle: () -> Unit = {},
    setGoBackFlag: (Boolean) -> Unit = {},
    setFab: (@Composable () -> Unit) -> Unit = {},
    resetFab: () -> Unit = {},
) {
    // all planning routes
    navigation(
        startDestination = PlanningRoutes.ListsBrowseScreen.route,
        route = PlanningRoutes.PlanningScreens.route
    ) {

        with(PlanningRoutes.ListsBrowseScreen) {
            composable(route) {
                saveTitle()
                setTitle(title)
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
                    saveTitle()
                    setTitle(list.name)
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
                saveTitle()
                setTitle(title)
                setGoBackFlag(true)
                setFab {
                    FabSaveNewItem {
                        /* onFab: TODO: save and return  */
                        resetFab()
                    }
                }
                Screen {
                    /* goBack */
                    restorePreviousTitle()
                    navController.popBackStack()
                }
            }
        }

        with(PlanningRoutes.ProductsBrowseScreen) {
            composable(
                route = route + "/{listId}?{listName}",
                arguments = listOf(
                    navArgument("listId") { type = NavType.StringType },
                    navArgument("listName") { type = NavType.StringType }
                ),
            ) { backStackEntry ->
                saveTitle()
                setTitle(backStackEntry.arguments?.getString("listName") ?: title)
                setGoBackFlag(true)
                setFab {
                    FabAddNewItem {
                        /* onFab: navigate to add item screen in order to add new product */
                        navController.navigate(PlanningRoutes.ProductsAddItemScreen.route)
                    }
                }
                Screen(
                    listId = backStackEntry.arguments
                        ?.getString("listId") ?: INVALID_SMOB_ITEM_ID,
                ) { product ->
                    /* navigateToProductDetails */
                    saveTitle()
                    setTitle(product.name)
                    setGoBackFlag(true)
                    resetFab()
                    navController.navigate(
                        PlanningRoutes.ProductDetailsScreen.route
                                + "/${product.id}?${product.name}"
                    )
                }
            }
        }


        with(PlanningRoutes.ProductsAddItemScreen) {
           composable(route) {
               saveTitle()
               setTitle(title)
               setGoBackFlag(true)
               setFab {
                   FabSaveNewItem {
                       /* onFab: TODO: save and return  */
                       resetFab()
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
                route = route + "/{productId}?{productName}",
                arguments = listOf(
                    navArgument("productId") { type = NavType.StringType },
                    navArgument("productName") { type = NavType.StringType }
                ),
            ) { backStackEntry ->
                saveTitle()
                setTitle(backStackEntry.arguments?.getString("productName") ?: title)
                setGoBackFlag(true)
                resetFab()
                Screen(
                    productId = backStackEntry.arguments
                        ?.getString("productId") ?: INVALID_SMOB_ITEM_ID,
                )
            }
        }

        with (PlanningRoutes.ShopsBrowseScreen) {
            composable(route) {
                saveTitle()
                setTitle(title)
                setGoBackFlag(true)
                resetFab()
                Screen { /* TODO: navigateToShopDetails */ }
            }
        }

        composable(route = PlanningRoutes.Screen3Screen.route) {
            PlanningRoutes.Screen3Screen.Screen()
        }

    }
}