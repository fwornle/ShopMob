package com.tanfra.shopmob.features.common.view

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NavDrawer(
    modifier: Modifier,
    drawerMenuItems: List<Pair<ImageVector, String>>,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    content: @Composable () -> Unit,
) {
    val selectedItem = remember { mutableStateOf(drawerMenuItems[0]) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                drawerMenuItems.forEach { item ->
                    NavigationDrawerItem(
                        modifier = modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        icon = { Icon(item.first, contentDescription = null) },
                        label = { Text(item.first.name) },
                        selected = item == selectedItem.value,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            selectedItem.value = item
                        }
                    )
                }
            }
        }
    ) { content() }

}

