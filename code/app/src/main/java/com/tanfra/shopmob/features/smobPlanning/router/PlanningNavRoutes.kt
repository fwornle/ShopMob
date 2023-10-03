package com.tanfra.shopmob.features.smobPlanning.router

sealed class PlanningNavRoutes(val route: String) {
    object PlanningListsScreen : PlanningNavRoutes("planningLists")
    object PlanningListsAddNewItem : PlanningNavRoutes("planningListsAddNewItem")
    object Screen3 : PlanningNavRoutes("screen3")
    object Settings : PlanningNavRoutes("settings")
}