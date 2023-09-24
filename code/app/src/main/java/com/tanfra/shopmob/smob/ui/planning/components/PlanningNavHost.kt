package com.tanfra.shopmob.smob.ui.planning.components

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tanfra.shopmob.smob.ui.planning.PlanningViewModel
import com.tanfra.shopmob.smob.ui.planning.PlanningNavRoutes
import com.tanfra.shopmob.smob.ui.planning.lists.components.SettingsScreen

@Composable
fun PlanningNavHost(
    context: Context,
    viewModel: PlanningViewModel,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = PlanningNavRoutes.PlanningListsScreen.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(route = PlanningNavRoutes.PlanningListsScreen.route) {
            PlanningScaffold(
                context = context,
                viewModel = viewModel,
            )
        }
        composable(route = PlanningNavRoutes.Settings.route) {
            SettingsScreen()
        }
    }

}


