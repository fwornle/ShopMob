package com.tanfra.shopmob.smob.ui.planning

sealed class PlanningNavRoutes(val route: String) {
    object PlanningListsScreen : PlanningNavRoutes("planningLists")
    object Settings : PlanningNavRoutes("settings")
    object Screen2 : PlanningNavRoutes("screen2")
    object Screen3 : PlanningNavRoutes("screen3")
}