package com.tanfra.shopmob.features.commonScaffold.presentation.view

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.tanfra.shopmob.smob.data.types.ImmutableList

@Composable
fun BottomBar(
    destinations: ImmutableList<TopLevelDestination>,
    currDest: NavDestination?,
    selTopLevelDest: TopLevelDestination?,
    setTopLevelDest: (TopLevelDestination?) -> Unit,
) {

    NavigationBar(
        modifier = Modifier
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
            )
            .height(70.dp),
    ) {
        destinations.items.forEach { daDest ->

            // "daDest" (for loop) is treated as "selected", when it can be found in the destination
            // tree of "currentDestination" (from NavHostController)
            val selected = currDest?.hierarchy?.any { it.route == daDest.route } == true

            // adjust hoisted state... to update all components depending on selTopLevelDestination
            if(selected && daDest.route != selTopLevelDest?.route) setTopLevelDest(daDest)

            NavigationBarItem(
                selected = selected,
                onClick = {
                    // install navTo of currently selected destination as onClick
                    destinations.items
                        .first { itemDest -> itemDest.route == daDest.route }
                        .let { it.navTo() }
                          },
                icon = {
                    val icon = if (selected) {
                        daDest.selectedIcon
                    } else {
                        daDest.unselectedIcon
                    }
                    Icon(
                        imageVector = ImageVector.vectorResource(icon),
                        modifier = Modifier.size(16.dp),
                        contentDescription = daDest.iconName
                    )
                },
                label = {
                    Text(
                        text = daDest.iconName
                    )
                }
            )

        }  // foreach (destination)

    }  // NavigationBar

}
