package com.tanfra.shopmob.features.smobPlanning.router

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.tanfra.shopmob.R
import com.tanfra.shopmob.app.SmobApp
import com.tanfra.shopmob.features.common.view.TopLevelDestination
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view.PlanningListsAddItemScreen
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view.PlanningListsBrowseScreen
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view.Screen3
import com.tanfra.shopmob.features.smobPlanning.presentation.view.products.view.PlanningProductDetailsScreen
import com.tanfra.shopmob.features.smobPlanning.presentation.view.products.view.PlanningProductsBrowseScreen
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.ui.zeUtils.consolidateListItem
import org.koin.androidx.compose.koinViewModel

sealed class PlanningRoutes {

    // navigation destinations
    protected val bottomBarDestinations = listOf(
        TopLevelDestination(
            route = ListsBrowsingScreen.route,
            selectedIcon = R.drawable.ic_baseline_view_list_24,
            unselectedIcon = R.drawable.ic_baseline_view_list_24,
            iconName = "Show Lists",
            title = "ShopMob"
        ), TopLevelDestination(
            route = ListsAddItemScreen.route,
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

    data object ListsBrowsingScreen : PlanningRoutes() {
        const val route = "planningListsBrowsing"

        // mechanism to filter out SmobList items which belong to the current user
        private fun onFilterList(items: List<SmobListATO>): List<SmobListATO> {
            // take out all items which have been deleted by swiping
            return items
                .filter { item -> item.groups
                    .map { group -> group.id }
                    .intersect((SmobApp.currUser?.groups ?: listOf()).toSet())
                    .any()
                }
                .filter { item -> item.status != ItemStatus.DELETED  }
                .map { item -> consolidateListItem(item) }
                .sortedWith(
                    compareBy { it.position }
                )
        }

        @Composable
        fun Screen(
            navController: NavHostController,
        ) = PlanningListsBrowseScreen(
            viewModel = koinViewModel(),
            navController = navController,
            bottomBarDestinations = bottomBarDestinations,
            drawerMenuItems = drawerMenuDestinations,
            onFilterList = { list -> onFilterList(list) }
        )
    }

    data object ListsAddItemScreen : PlanningRoutes() {
        const val route = "planningListsAddItem"

        @Composable
        fun Screen(
            navController: NavHostController,
        ) = PlanningListsAddItemScreen(
            viewModel = koinViewModel(),
            navController = navController,
            bottomBarDestinations = bottomBarDestinations,
            drawerMenuItems = drawerMenuDestinations,
        )
    }

    data object SelectedListProductsBrowseScreen : PlanningRoutes() {
        const val route = "planningProductsBrowsing"

        @Composable
        fun Screen(
            navController: NavHostController,
            listId: String,
            listName: String,
        ) = PlanningProductsBrowseScreen(
            viewModel = koinViewModel(),
            navController = navController,
            bottomBarDestinations = bottomBarDestinations,
            drawerMenuItems = drawerMenuDestinations,
            listId = listId,
            listName = listName,
        )
    }

    data object SelectedProductDetailsScreen : PlanningRoutes() {
        const val route = "planningProductDetails"

        @Composable
        fun Screen(
            navController: NavHostController,
            productId: String,
            productName: String,
        ) = PlanningProductDetailsScreen(
            viewModel = koinViewModel(),
            navController = navController,
            bottomBarDestinations = bottomBarDestinations,
            drawerMenuItems = drawerMenuDestinations,
            productId = productId,
            productName = productName,
        )
    }

    data object Screen3Screen : PlanningRoutes() {
        const val route = "planningListsSreen3"

        @Composable
        fun Screen() = Screen3()
    }

}