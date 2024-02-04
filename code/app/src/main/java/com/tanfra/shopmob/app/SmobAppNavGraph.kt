package com.tanfra.shopmob.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.tanfra.shopmob.app.Constants.INVALID_ITEM_ID
import com.tanfra.shopmob.features.common.view.FabAddNewItem
import com.tanfra.shopmob.features.common.view.FabSaveNewItem
import com.tanfra.shopmob.features.commonScaffold.router.ScaffoldRoutes
import com.tanfra.shopmob.features.smobAdmin.router.AdminRoutes
import com.tanfra.shopmob.features.smobPlanning.router.PlanningRoutes
import com.tanfra.shopmob.features.smobPlanning.router.ShopsRoutes
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO

fun NavGraphBuilder.routes(
    navController: NavHostController,
    setNewScaffold: (String, Boolean, (@Composable () -> Unit)?) -> Unit = { _, _, _ -> },
    restorePreviousScaffold: () -> Unit = {},
    setNewFab: ((@Composable () -> Unit)?) -> Unit = {},
) {
    // all TopLevel routes
    navigation(
        startDestination = PlanningRoutes.PlanningScreens.route,
        route = ScaffoldRoutes.ScaffoldScreen.route
    ) {

        // TopLevel: Planning
        navigation(
            startDestination = PlanningRoutes.PlanningStart.route,
            route = PlanningRoutes.PlanningScreens.route
        ) {

            // (invisible) start-up screen (to allow Scaffold control for actual "startDestion")
            with(PlanningRoutes.PlanningStart) {
                composable(route) {

                    // prepare scaffold of actual "startDestination"
                    setNewScaffold(PlanningRoutes.ListsBrowseScreen.title, false) {
                        /* FAB: navigate to add item screen in order to add new list */
                        FabAddNewItem {
                            setNewScaffold(PlanningRoutes.ListsAddItemScreen.title, true, null)
                            navController.navigate(PlanningRoutes.ListsAddItemScreen.route)
                        }
                    }

                    // navigate to actual "startDestination"
                    navController.navigate(PlanningRoutes.ListsBrowseScreen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }

                }
            }

            // actual "startupDestination"
            with(PlanningRoutes.ListsBrowseScreen) {
                composable(route) {
                    Screen {
                        /* navigateToList */
                            list: SmobListATO ->

                        // adjust Scaffold items
                        setNewScaffold(list.name, true) {
                            FabAddNewItem {
                                /* onFab: navigate to "add new product" (on list) screen */
                                setNewScaffold(
                                    "${PlanningRoutes.ProductsAddItemScreen.title} to '${list.name}'",
                                    true,
                                    null    // FAB only appears when "save" conditions have been met
                                )
                                navController.navigate(
                                    PlanningRoutes.ProductsAddItemScreen.route
                                            + "/${list.id}"
                                )
                            }
                        }

                        // navigate to "browse products" (of selected list)
                        navController.navigate(
                            PlanningRoutes.ProductsBrowseScreen.route + "/${list.id}"
                        )
                    }
                }
            }

            with(PlanningRoutes.ListsAddItemScreen) {
                composable(route) {
                    Screen(setNewFab) {
                        /* goBack */
                        restorePreviousScaffold()
                        navController.popBackStack()
                    }
                }
            }

            with(PlanningRoutes.ProductsBrowseScreen) {
                composable(
                    route = "$route/{listId}",
                    arguments = listOf(
                        navArgument("listId") { type = NavType.StringType },
                    ),
                ) { backStackEntry ->
                    val listId = backStackEntry.arguments?.getString("listId") ?: INVALID_ITEM_ID
                    Screen(listId) { product ->
                        /* navigateToProductDetails */
                        setNewScaffold(
                            product.name,
                            true,
                            null
                        )
                        navController.navigate(
                            PlanningRoutes.ProductDetailsScreen.route
                                    + "/${product.id}?${product.name}"
                        )
                    }
                }
            }


            with(PlanningRoutes.ProductsAddItemScreen) {
                composable(
                    route = "$route/{listId}",
                    arguments = listOf(
                        navArgument("listId") { type = NavType.StringType },
                    )
                ) { backStackEntry ->
                    val listId = backStackEntry.arguments?.getString("listId") ?: INVALID_ITEM_ID
                    setNewScaffold(title, true) {
                        FabSaveNewItem {
                            /* onFab: TODO: save and return  */
                            setNewFab(null)
                        }
                    }
                    Screen(
                        selectedListId = listId,
                        navigateToShopSelect = {
                            navController.navigate(ShopsRoutes.ShopsBrowseScreen.route)
                        },
                        goBack = {
                            restorePreviousScaffold()
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
                    Screen(
                        productId = backStackEntry.arguments
                            ?.getString("productId") ?: INVALID_ITEM_ID,
                    )
                }
            }

        }

        // TopLevel: Shops
        navigation(
            startDestination = ShopsRoutes.ShopsStart.route,
            route = ShopsRoutes.ShopsScreens.route
        ) {

            // (invisible) start-up screen (to allow Scaffold control for actual "startDestion")
            with(ShopsRoutes.ShopsStart) {
                composable(route) {

                    // prepare scaffold of actual "startDestination"
                    setNewScaffold(ShopsRoutes.ShopsBrowseScreen.title, false, null)
//                    {
//                        /* FAB: navigate to add item screen in order to add new shop */
//                        FabAddNewItem {
//                            setNewScaffold(ShopsRoutes.ShopAddItemScreen.title, true, null)
//                            navController.navigate(ShopsRoutes.ShopAddItemScreen.route)
//                        }
//                    }

                    // navigate to actual "startDestination"
                    navController.navigate(ShopsRoutes.ShopsBrowseScreen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }

                }
            }

            with(ShopsRoutes.ShopsBrowseScreen) {
                composable(route) {
                    Screen { shop ->
                        /* navigateToShopDetails */
                        setNewScaffold(shop.name, true, null)
                        navController.navigate(
                            ShopsRoutes.ShopDetailsScreen.route
                                    + "/${shop.id}"
                        )
                    }
                }
            }

            with(ShopsRoutes.ShopDetailsScreen) {
                composable(
                    route = "$route/{shopId}",
                    arguments = listOf(
                        navArgument("shopId") { type = NavType.StringType },
                    ),
                ) { backStackEntry ->
                    Screen(
                        shopId = backStackEntry.arguments
                            ?.getString("shopId") ?: INVALID_ITEM_ID,
                    )
                }
            }

        }

        // TopLevel: Administration
        navigation(
            startDestination = AdminRoutes.AdminStart.route,
            route = AdminRoutes.AdminScreens.route
        ) {

            // (invisible) start-up screen (to allow Scaffold control for actual "startDestion")
            with(AdminRoutes.AdminStart) {
                composable(route) {

                    // prepare scaffold of actual "startDestination"
                    setNewScaffold(AdminRoutes.AdminBrowseScreen.title, false, null)

                    // navigate to actual "startDestination"
                    navController.navigate(AdminRoutes.AdminBrowseScreen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }

                }
            }

            with(AdminRoutes.AdminBrowseScreen) {
                composable(route) {
                    Screen()
                }
            }

        }

    }

}