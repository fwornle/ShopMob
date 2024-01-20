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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import com.tanfra.shopmob.smob.data.types.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NavDrawer(
    modifier: Modifier,
    drawerMenuItems: ImmutableList<TopLevelDestination>,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    content: @Composable () -> Unit,
) {
    val selectedItem = remember { mutableStateOf(drawerMenuItems.items[0]) }

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                drawerMenuItems.items.forEach { item ->

                    val selected = item == selectedItem.value

                    NavigationDrawerItem(
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        icon = @Composable {
                            val icon = if (selected) {
                                item.selectedIcon
                            } else {
                                item.unselectedIcon
                            }
                            Icon(
                                imageVector = ImageVector.vectorResource(icon),
                                modifier = Modifier.size(16.dp),
                                contentDescription = item.iconName
                            )
                        },
                        label = { Text(item.title) },
                        selected = selected,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            selectedItem.value = item

                            // navigate to target
                            item.navTo()
                        }
                    )
                }
            }
        }
    ) { content() }

}
