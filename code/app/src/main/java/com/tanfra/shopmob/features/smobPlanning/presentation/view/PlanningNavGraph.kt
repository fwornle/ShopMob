package com.tanfra.shopmob.features.smobPlanning.presentation.view

import android.content.Context
import android.os.Vibrator
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tanfra.shopmob.R
import com.tanfra.shopmob.app.SmobApp
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view.PlanningListsAddItemScreen
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view.Screen3
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view.SettingsScreen
import com.tanfra.shopmob.features.smobPlanning.router.PlanningRouter
import com.tanfra.shopmob.features.smobPlanning.router.PlanningRoutes
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.ui.zeUtils.consolidateListItem
import com.tanfra.shopmob.smob.ui.zeUtils.vibrateDevice

@Composable
fun PlanningNavGraph(
    context: Context,
    navController: NavHostController = rememberNavController(),
) {

    NavHost(
        navController = navController,
        startDestination = PlanningRouter.route
    ) {
        // all planning routes
//        navigation(startDestination = "planningLists", route = "planning") {
            composable(route = PlanningRouter.route) {
                PlanningRouter.Screen(
                    navController = navController,
                    onFilterList = { list -> onFilterList(list) },
                    onSwipeIllegalTransition = { onSwipeIllegalTransition(context) },
                    onClickItem = { item -> sendToList(navController, item) },
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

// illegal swipe transition --> vibrate phone
private fun onSwipeIllegalTransition(context: Context) {
    val vib = context.getSystemService(Vibrator::class.java)
    vibrateDevice(vib, 150)
}



