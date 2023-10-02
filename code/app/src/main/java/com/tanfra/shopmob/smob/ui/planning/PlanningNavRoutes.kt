package com.tanfra.shopmob.smob.ui.planning

sealed class PlanningNavRoutes(val route: String) {
    object PlanningListsScreen : PlanningNavRoutes("planningLists")
    object PlanningListsAddNewItem : PlanningNavRoutes("planningListsAddNewItem")
    object Screen3 : PlanningNavRoutes("screen3")
    object Settings : PlanningNavRoutes("settings")
}