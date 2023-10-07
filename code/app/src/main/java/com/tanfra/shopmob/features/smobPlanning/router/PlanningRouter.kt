package com.tanfra.shopmob.features.smobPlanning.router

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

data object PlanningRouter {
    const val param = "id"
    const val route = "lists/{$param}"

    fun route(id: String) = "lists/$id"

//    @Composable
//    fun Screen(
//        navcontroller: NavHostController,
//    ) =
//        PlanningListsBrowseScreen(
//            navController = navcontroller,
//        )
}
