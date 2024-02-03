package com.tanfra.shopmob.features.commonScaffold.presentation.view

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.tanfra.shopmob.smob.data.types.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NavDrawer(
    modifier: Modifier,
    destinations: ImmutableList<TopLevelDestination>,
    currDest: NavDestination?,
    selTopLevelDest: TopLevelDestination?,
    setTopLevelDest: (TopLevelDestination?) -> Unit,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))

                destinations.items.forEach { daDest ->

                    // "daDest" (for loop) is treated as "selected", when it can be found in the destination
                    // tree of "currentDestination" (from NavHostController)
                    val selected = currDest?.hierarchy?.any { it.route == daDest.route } == true

                    // adjust hoisted state... to update all components depending on selTopLevelDestination
                    if(selected && daDest.route != selTopLevelDest?.route) setTopLevelDest(daDest)

                    NavigationDrawerItem(
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        icon = @Composable {
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
                        label = { Text(daDest.title) },
                        selected = selected,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }

                            // set (hoisted) state - triggers adjustment of (linked) BottomBar
                            setTopLevelDest(daDest)

                            // navigate to target
                            daDest.navTo()
                        }
                    )
                }
            }
        }
    ) { content() }

}
