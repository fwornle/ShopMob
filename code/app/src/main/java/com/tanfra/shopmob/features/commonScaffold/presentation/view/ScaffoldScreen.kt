package com.tanfra.shopmob.features.commonScaffold.presentation.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tanfra.shopmob.R
import com.tanfra.shopmob.app.routes
import com.tanfra.shopmob.features.commonScaffold.presentation.ScaffoldViewModelMvi
import com.tanfra.shopmob.features.commonScaffold.presentation.model.ScaffoldAction
import com.tanfra.shopmob.features.commonScaffold.presentation.model.ScaffoldEvent
import com.tanfra.shopmob.features.smobPlanning.router.PlanningRoutes
import com.tanfra.shopmob.smob.data.types.ImmutableList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldScreen(
    viewModel: ScaffoldViewModelMvi,
    startTitle: String,
    startDestination: String,
    bottomBarDestinations: List<TopLevelDestination> = listOf(),
    drawerMenuItems: ImmutableList<Pair<ImageVector, String>> = ImmutableList(listOf()),
) {
    // lifecycle aware collection of viewState flow
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewState by viewModel.viewStateFlow
        .collectAsStateWithLifecycle(
            initialValue = ScaffoldViewState(),
            lifecycleOwner = lifecycleOwner,
            minActiveState = Lifecycle.State.STARTED,
            context = viewModel.viewModelScope.coroutineContext,
        )

    // actions to be triggered (once) on CREATED
    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.process(ScaffoldAction.CheckConnectivity)
            viewModel.process(ScaffoldAction.SetNewTitle(startTitle))
        }
    }

    // actions to be triggered (once) on STARTED
    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

            // collect event flow - triggers reactions to signals from VM
            viewModel.eventFlow.collectLatest { event ->
                when (event) {
                    is ScaffoldEvent.Refreshing -> { /* TODO */ }  // ???
                }
            }
        }
    }


    // local store
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    // setters for TopAppBar title management
    val setNewTitle = { daTitle: String -> viewModel.process(ScaffoldAction.SetNewTitle(daTitle)) }
    val restorePreviousTitle = { viewModel.process(ScaffoldAction.SetPreviousTitle) }

    // navigation root
    val navController: NavHostController = rememberNavController()

    // trace re-composes
    Timber.i("recomposing 'ScreenScaffold' - title: ${viewState.currentTitle}")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    // Timber.i("recomposing 'TopAppBar' title: $cachedTitle")
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = viewState.currentTitle,
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
                    if (viewState.currentGoBackFlag) {
                        IconButton(
                            modifier = Modifier,
                            onClick = {
                                restorePreviousTitle()
                                navController.popBackStack() },
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
                            .let { setNewTitle(it.title) }
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
        floatingActionButton = { viewState.currentFab?.let { it() } },
        floatingActionButtonPosition = FabPosition.End,
    ) { paddingValues ->
        NavDrawer(
            modifier = Modifier.padding(paddingValues),
            drawerMenuItems = drawerMenuItems,
            drawerState = drawerState,
            coroutineScope = scope,
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                routes(
                    navController = navController,
                    setNewTitle = setNewTitle,
                    restorePreviousTitle = restorePreviousTitle,
                    setGoBackFlag = { daFlag: Boolean ->
                        viewModel.process(ScaffoldAction.SetGoBackFlag(daFlag)) },
                    setFab = { newFab: (@Composable () -> Unit)? ->
                        viewModel.process(ScaffoldAction.SetNewFab(newFab)) },
                )
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
            route = PlanningRoutes.ListsBrowseScreen.route,
            selectedIcon = R.drawable.ic_baseline_view_list_24,
            unselectedIcon = R.drawable.ic_baseline_view_list_24,
            iconName = "Show Lists",
            title = "ShopMob"
        ), TopLevelDestination(
            route = PlanningRoutes.ListsAddItemScreen.route,
            selectedIcon = R.drawable.ic_add,
            unselectedIcon = R.drawable.ic_add,
            iconName = "New List",
            title = "Add New SmobList"
        ), TopLevelDestination(
            route = PlanningRoutes.ShopsBrowseScreen.route,
            selectedIcon = R.drawable.ic_baseline_shopping_cart_24,
            unselectedIcon = R.drawable.ic_baseline_shopping_cart_24,
            iconName = "Shops",
            title = "Shops"
        )
    )

    // drawer menu destinations
    val drawerMenuDestinations = listOf(
        Pair(Icons.Default.Favorite, "Favorite"),
        Pair(Icons.Default.Face, "Face"),
        Pair(Icons.Default.Email, "Email"),
    )

    MaterialTheme {
        ScaffoldScreen(
            viewModel = koinViewModel(),
            startTitle = "App",
            startDestination = "AppStartDest",
            bottomBarDestinations = topLevelDestinations,
            drawerMenuItems = ImmutableList(drawerMenuDestinations)
        )
    }
}
