package com.tanfra.shopmob.features.common.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tanfra.shopmob.R
import com.tanfra.shopmob.features.smobPlanning.router.PlanningListsRoutes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenScaffold(
    title: String,
    canGoBack: Boolean = false,
    onBack: () -> Unit = {},
    bottomBarDestinations: List<TopLevelDestination> = listOf(),
    drawerMenuItems: List<Pair<ImageVector, String>> = listOf(),
    isFabVisible: Boolean = false,
    navController: NavHostController = rememberNavController(),
    content: @Composable () -> Unit,
) {
    // local store
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    // BottomBar navigation can alter the title (currently: local state)
    var cachedTitle by remember { mutableStateOf(title) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = cachedTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        textAlign = TextAlign.Start,
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    if (canGoBack) {
                        IconButton(
                            modifier = Modifier,
                            onClick = onBack,
                        ) {
                            Icon(
                                modifier = Modifier.padding(start = 4.dp),
                                tint = Color.White,
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null,
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) drawerState.open()
                                    else drawerState.close()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                contentDescription = "Toggle Drawer Menu"
                            )
                        }
                    }
                },
            )
        },
        bottomBar = {
            if(bottomBarDestinations.isNotEmpty()) {
                BottomBar(
                    destinations = bottomBarDestinations,
                    currentDestination = navController.currentBackStackEntryAsState().value?.destination,
                    onNavigateToDestination = { route: String ->
                        bottomBarDestinations
                            .first { dest -> route == dest.route }
                            .let { cachedTitle = it.title }
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            restoreState = true
                            launchSingleTop = true
                        }
                    }
                )
            }  // any BottomBar destinations at all?
        },
        floatingActionButton = {
            if (isFabVisible) {
                FloatingActionButton(
                    onClick = { /* some reaction */ }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Save")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { paddingValues ->
        NavDrawer(
            modifier = Modifier.padding(paddingValues),
            drawerMenuItems = drawerMenuItems,
            drawerState = drawerState,
            coroutineScope = scope,
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                content()
            }
        }
    }
}


@Preview
@Composable
private fun ScreenScaffoldPreview() {

    // navigation destinations
    val topLevelDestinations = listOf(
        TopLevelDestination(
            route = PlanningListsRoutes.BrowsingScreen.route,
            selectedIcon = R.drawable.ic_baseline_view_list_24,
            unselectedIcon = R.drawable.ic_baseline_view_list_24,
            iconName = "Show Lists",
            title = "ShopMob"
        ), TopLevelDestination(
            route = PlanningListsRoutes.AddItemScreen.route,
            selectedIcon = R.drawable.ic_add,
            unselectedIcon = R.drawable.ic_add,
            iconName = "New List",
            title = "Add New SmobList"
        ), TopLevelDestination(
            route = PlanningListsRoutes.Screen3Screen.route,
            selectedIcon = R.drawable.ic_location,
            unselectedIcon = R.drawable.ic_save,
            iconName = "Screen 3",
            title = "Screen 3"
        )
    )

    // drawer menu destinations
    val drawerMenuDestinations = listOf(
        Pair(Icons.Default.Favorite, "Favorite"),
        Pair(Icons.Default.Face, "Face"),
        Pair(Icons.Default.Email, "Email"),
    )

    MaterialTheme {
        ScreenScaffold(
            title = "App",
            bottomBarDestinations = topLevelDestinations,
            drawerMenuItems = drawerMenuDestinations,
        ) {
            Text("Test Screen")
        }
    }
}
