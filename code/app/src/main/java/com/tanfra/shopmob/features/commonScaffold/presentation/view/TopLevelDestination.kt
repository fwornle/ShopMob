package com.tanfra.shopmob.features.commonScaffold.presentation.view

import androidx.compose.runtime.Composable

// data type for the definition of destinations reachable via the BottomBar
data class TopLevelDestination(
    val route: String,
    val navTo: () -> Unit,
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val iconName: String,

    // behavioral elements
    val title: String,
    val goBackFlag: Boolean,
    val fab: (@Composable () -> Unit)?,
)