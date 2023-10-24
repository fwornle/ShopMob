package com.tanfra.shopmob.features.smobPlanning.presentation.view

import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.tanfra.shopmob.R
import com.tanfra.shopmob.app.SmobApp
import com.tanfra.shopmob.features.smobPlanning.router.PlanningListsRoutes
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.ui.zeUtils.consolidateListItem

@Composable
fun PlanningNavGraph(
    navController: NavHostController = rememberNavController(),
) {

    NavHost(
        navController = navController,
        startDestination = "planningLists"
    ) {
        // all planning routes
        navigation(
            startDestination = PlanningListsRoutes.BrowsingScreen.route,
            route = "planningLists"
        ) {

            composable(route = PlanningListsRoutes.BrowsingScreen.route) {
                PlanningListsRoutes.BrowsingScreen.Screen(
                    navController = navController,
                    onFilterList = { list -> onFilterList(list) },
                    onClickItem = { item -> sendToList(navController, item) },
                )
            }

            composable(route = PlanningListsRoutes.AddItemScreen.route) {
                PlanningListsRoutes.AddItemScreen.Screen(
                    navController = navController,
                    goBack = { navController.popBackStack() }
                )
            }

            composable(route = PlanningListsRoutes.Screen3Screen.route) {
                PlanningListsRoutes.Screen3Screen.Screen()
            }

        }
    }
}


// mechanism to filter out list items
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


private fun sendToList(navController: NavHostController, item: SmobListATO) {

    // communicate the ID and name of the selected item (= shopping list)
    val bundle = bundleOf(
        "listId" to item.id,
        "listName" to item.name,
    )

//        // use the navigationCommand live data to navigate between the fragments
//        navigationCommand.postValue(
//            NavigationCommand.ToWithBundle(
//                R.id.smobPlanningProductsTableFragment,
//                bundle
//            )
//        )

    navController.navigate(
        R.id.smobPlanningProductsTableFragment,
        bundle
    )

}
