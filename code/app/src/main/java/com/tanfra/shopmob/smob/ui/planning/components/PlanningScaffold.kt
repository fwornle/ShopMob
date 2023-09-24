package com.tanfra.shopmob.smob.ui.planning.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.firebase.ui.auth.AuthUI
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.auth.SmobAuthActivity
import com.tanfra.shopmob.smob.ui.planning.PlanningViewModel
import com.tanfra.shopmob.smob.ui.planning.PlanningNavRoutes
import com.tanfra.shopmob.smob.ui.planning.lists.components.PlanningListsScreen
import com.tanfra.shopmob.smob.ui.planning.lists.components.Screen2
import com.tanfra.shopmob.smob.ui.planning.lists.components.Screen3
import com.tanfra.shopmob.smob.ui.zeUtils.TopLevelDestination
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanningScaffold(
    context: Context,
    viewModel: PlanningViewModel
) {

    val topLevelDestinations = listOf(
        TopLevelDestination(
            route = PlanningNavRoutes.PlanningListsScreen.route,
            selectedIcon = R.drawable.ic_no_data,
            unselectedIcon = R.drawable.ic_baseline_delete_forever_24,
            iconText = "smobLists"
        ), TopLevelDestination(
            route = PlanningNavRoutes.Screen2.route,
            selectedIcon = R.drawable.ic_baseline_shopping_cart_24,
            unselectedIcon = R.drawable.ic_baseline_broken_image_24,
            iconText = "Screen2"
        ), TopLevelDestination(
            route = PlanningNavRoutes.Screen3.route,
            selectedIcon = R.drawable.ic_location,
            unselectedIcon = R.drawable.ic_save,
            iconText = "Screen3"
        )
    )

    val showBottomBar = remember { mutableStateOf(true) }
    val title = remember {
        mutableStateOf("Home")
    }
    val navController = rememberNavController()


    // Scaffold state
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // icons to mimic drawer destinations
    val items = listOf(
        Icons.Default.Favorite,
        Icons.Default.Face,
        Icons.Default.Email
    )
    val selectedItem = remember { mutableStateOf(items[0]) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = topLevelDestinations[0].iconText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
//                        text = title.value,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
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
                },
                actions = {
                    IconButton(
//                        modifier = Modifier
//                            .clickable {
//                                navigateTo(PlanningNavRoutes.Settings.route)
//                            }
//                            .padding(8.dp),
                        onClick = {
                            // logout
                            AuthUI.getInstance()
                                .signOut(context)
                                .addOnCompleteListener {
                                    // user is now signed out -> redirect to login screen
                                    context.startActivity(Intent(context, SmobAuthActivity::class.java))
                                }
                        }) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = "Logout"
                        )
                    }
                },
            )
        },
        bottomBar = {
            if (showBottomBar.value) {
                PlanningBottomBar(destinations = topLevelDestinations,
                    currentDestination = navController.currentBackStackEntryAsState().value?.destination,
                    onNavigateToDestination = {
                        title.value = when (it) {
                            "smobLists" -> "SmobLists"
                            "screen2" -> "Screen 2"
                            else -> {
                                "Screen 3"
                            }
                        }
                        navController.navigate(it) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            restoreState = true
                            launchSingleTop = true
                        }
                    })
            }
        }
    ) { paddingValues ->

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Spacer(Modifier.height(12.dp))
                    items.forEach { item ->
                        NavigationDrawerItem(
                            icon = { Icon(item, contentDescription = null) },
                            label = { Text(item.name) },
                            selected = item == selectedItem.value,
                            onClick = {
                                scope.launch { drawerState.close() }
                                selectedItem.value = item
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            }
        ) {

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                NavHost(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    navController = navController,
                    startDestination = PlanningNavRoutes.PlanningListsScreen.route,
                ) {

                    composable(route = PlanningNavRoutes.PlanningListsScreen.route) {
                        PlanningListsScreen(viewModel, paddingValues)
                    }
                    composable(route = PlanningNavRoutes.Screen2.route) {
                        Screen2()
                    }
                    composable(route = PlanningNavRoutes.Screen3.route) {
                        Screen3()
                    }

                }

            }
        }

    }  // NavDrawer

}