package com.tanfra.shopmob.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tanfra.shopmob.features.common.view.ScreenScaffold
import com.tanfra.shopmob.features.common.view.TopLevelDestination

@Composable
fun SmobAppScaffold(
    title: String,
    bottomBarDestinations: List<TopLevelDestination>,
    drawerMenuItems: List<Pair<ImageVector, String>>,
) {
    // hoist state for topBar "title" to be able to change it from within the content pages
    var cachedTitle by remember { mutableStateOf(title) }
    val setCachedTitle = { daTitle: String -> cachedTitle = daTitle }

    // hoist state for topBar "goBackFlag" to be able to change it from within the content pages
    var cachedGoBackFlag by remember { mutableStateOf(false) }
    val setGoBackFlag = { daFlag: Boolean -> cachedGoBackFlag = daFlag }

    ScreenScaffold(
        title = cachedTitle,
        onSetTitle = setCachedTitle,
        goBackFlag = cachedGoBackFlag,
        onSetGoBackFlag = setGoBackFlag,
        bottomBarDestinations = bottomBarDestinations,
        drawerMenuItems = drawerMenuItems,
        navController = rememberNavController(),
    ) {
            navController: NavHostController,
            onSetGoBackFlag: (Boolean) -> Unit ->
        SmobAppNavGraph(navController, onSetGoBackFlag)
    }
}
