package com.tanfra.shopmob.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.rememberNavController
import com.tanfra.shopmob.features.common.view.ScreenScaffold
import com.tanfra.shopmob.features.common.view.TopLevelDestination
import com.tanfra.shopmob.smob.data.types.ImmutableList

@Composable
fun SmobAppScaffold(
    title: String,
    bottomBarDestinations: List<TopLevelDestination>,
    drawerMenuItems: ImmutableList<Pair<ImageVector, String>>,
) {
    // hoist state for topBar "title" to be able to change it from within the content pages
    var cachedTitle by remember { mutableStateOf(title) }
    val setTitle = { daTitle: String -> cachedTitle = daTitle }

    // hoist state for topBar "goBackFlag" to be able to change it from within the content pages
    var cachedGoBackFlag by remember { mutableStateOf(false) }
    val setGoBackFlag = { daFlag: Boolean -> cachedGoBackFlag = daFlag }

    val navController = rememberNavController()

    ScreenScaffold(
        title = cachedTitle,
        setTitle = setTitle,
        goBackFlag = cachedGoBackFlag,
        bottomBarDestinations = bottomBarDestinations,
        drawerMenuItems = drawerMenuItems,
        navController = navController,
    ) {
        SmobAppNavGraph(navController, setGoBackFlag)
    }
}
