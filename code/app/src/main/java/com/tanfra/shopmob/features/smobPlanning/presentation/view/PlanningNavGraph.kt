package com.tanfra.shopmob.features.smobPlanning.presentation.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tanfra.shopmob.R
import com.tanfra.shopmob.features.common.view.TopLevelDestination
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view.PlanningListsAddItemScreen
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view.PlanningListsBrowseScreen
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view.Screen3
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view.SettingsScreen
import com.tanfra.shopmob.features.smobPlanning.router.PlanningRoutes

@Composable
fun PlanningNavGraph(
    navController: NavHostController = rememberNavController(),
) {
    // navigation destinations
    val topLevelDestinations = listOf(
        TopLevelDestination(
            route = PlanningRoutes.PlanningListsScreen.route,
            selectedIcon = R.drawable.ic_baseline_view_list_24,
            unselectedIcon = R.drawable.ic_baseline_view_list_24,
            iconName = "Show Lists",
            title = "ShopMob"
        ), TopLevelDestination(
            route = PlanningRoutes.PlanningListsAddNewItem.route,
            selectedIcon = R.drawable.ic_add,
            unselectedIcon = R.drawable.ic_add,
            iconName = "New List",
            title = "Add New SmobList"
        ), TopLevelDestination(
            route = PlanningRoutes.Screen3.route,
            selectedIcon = R.drawable.ic_location,
            unselectedIcon = R.drawable.ic_save,
            iconName = "Screen 3",
            title = "Screen 3"
        )
    )

    // drawer menu states
    val drawerMenuItems = listOf(
        Pair(Icons.Default.Favorite, "Favorite"),
        Pair(Icons.Default.Face, "Favorite"),
        Pair(Icons.Default.Email, "Favorite"),
    )

    NavHost(
        navController = navController,
        startDestination = PlanningRoutes.PlanningListsScreen.route
    ) {
        // all planning routes
//        navigation(startDestination = "planningLists", route = "planning") {
            composable(route = PlanningRoutes.PlanningListsScreen.route) {
                PlanningListsBrowseScreen(
                    topLevelDestinations,
                    drawerMenuItems,
                    navController
                )
            }
            composable(route = PlanningRoutes.PlanningListsAddNewItem.route) {
                PlanningListsAddItemScreen { navController.navigate("planningLists") }
            }
            composable(route = PlanningRoutes.Screen3.route) {
                Screen3()
            }
            composable(route = PlanningRoutes.Settings.route) {
                SettingsScreen()
            }
        }
//    }
}