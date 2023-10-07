package com.tanfra.shopmob.features.smobPlanning.router

sealed class PlanningRoutes(val route: String) {
    data object PlanningListsScreen : PlanningRoutes("planningLists")
    data object PlanningListsAddNewItem : PlanningRoutes("planningListsAddNewItem")
    data object Screen3 : PlanningRoutes("screen3")
    data object Settings : PlanningRoutes("settings")
}