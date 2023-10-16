package com.tanfra.shopmob.features.smobPlanning.router

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.tanfra.shopmob.R
import com.tanfra.shopmob.features.common.view.TopLevelDestination
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view.PlanningListsAddItemScreen
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view.PlanningListsBrowseScreen
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view.Screen3
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import org.koin.androidx.compose.koinViewModel

sealed class PlanningListsRoutes {

    // navigation destinations
    protected val bottomBarDestinations = listOf(
        TopLevelDestination(
            route = BrowsingScreen.route,
            selectedIcon = R.drawable.ic_baseline_view_list_24,
            unselectedIcon = R.drawable.ic_baseline_view_list_24,
            iconName = "Show Lists",
            title = "ShopMob"
        ), TopLevelDestination(
            route = AddItemScreen.route,
            selectedIcon = R.drawable.ic_add,
            unselectedIcon = R.drawable.ic_add,
            iconName = "New List",
            title = "Add New SmobList"
        ), TopLevelDestination(
            route = Screen3Screen.route,
            selectedIcon = R.drawable.ic_location,
            unselectedIcon = R.drawable.ic_save,
            iconName = "Screen 3",
            title = "Screen 3"
        )
    )

    // drawer menu destinations
    protected val drawerMenuDestinations = listOf(
        Pair(Icons.Default.Favorite, "Favorite"),
        Pair(Icons.Default.Face, "Face"),
        Pair(Icons.Default.Email, "Email"),
    )

    data object BrowsingScreen : PlanningListsRoutes() {
        const val route = "planningListsBrowsing"

        @Composable
        fun Screen(
            navController: NavHostController,
            onFilterList: (List<SmobListATO>) -> List<SmobListATO>,
            onClickItem: (SmobListATO) -> Unit,
        ) = PlanningListsBrowseScreen(
            viewModel = koinViewModel(),
            navController = navController,
            bottomBarDestinations = bottomBarDestinations,
            drawerMenuItems = drawerMenuDestinations,
            onFilterList = onFilterList,
            onClickItem = onClickItem,
        )
    }

    data object AddItemScreen : PlanningListsRoutes() {
        const val route = "planningListsAddItem"

        @Composable
        fun Screen(
            navController: NavHostController,
            goBack: () -> Unit,
        ) = PlanningListsAddItemScreen(
            viewModel = koinViewModel(),
            navController = navController,
            bottomBarDestinations = bottomBarDestinations,
            drawerMenuItems = drawerMenuDestinations,
            onNavigateBack = goBack
        )
    }

    data object Screen3Screen : PlanningListsRoutes() {
        const val route = "planningListsSreen3"

        @Composable
        fun Screen() = Screen3()
    }

}