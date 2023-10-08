package com.tanfra.shopmob.features.smobPlanning.router

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.tanfra.shopmob.R
import com.tanfra.shopmob.features.common.view.TopLevelDestination
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view.PlanningListsBrowseScreen
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import org.koin.androidx.compose.koinViewModel

data object PlanningRouter {
    const val route = "planningLists"

    // navigation destinations
    private val bottomBarDestinations = listOf(
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

    // drawer menu destinations
    private val drawerMenuDestinations = listOf(
        Pair(Icons.Default.Favorite, "Favorite"),
        Pair(Icons.Default.Face, "Face"),
        Pair(Icons.Default.Email, "Email"),
    )

    @Composable
    fun Screen(
        navController: NavHostController,
        onFilterList: (List<SmobListATO>) -> List<SmobListATO>,
        onSwipeIllegalTransition: () -> Unit,
        onClickItem: (SmobListATO) -> Unit,
    ) = PlanningListsBrowseScreen(
        viewModel = koinViewModel(),
        bottomBarDestinations = bottomBarDestinations,
        drawerMenuItems = drawerMenuDestinations,
        navController = navController,
        onFilterList = onFilterList,
        onSwipeIllegalTransition = onSwipeIllegalTransition,
        onClickItem = onClickItem,
    )

}
