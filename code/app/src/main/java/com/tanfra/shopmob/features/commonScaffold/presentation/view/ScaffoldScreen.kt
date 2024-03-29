package com.tanfra.shopmob.features.commonScaffold.presentation.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tanfra.shopmob.app.routes
import com.tanfra.shopmob.features.commonScaffold.presentation.ScaffoldViewModelMvi
import com.tanfra.shopmob.features.commonScaffold.presentation.model.ScaffoldAction
import com.tanfra.shopmob.features.commonScaffold.presentation.model.ScaffoldEvent
import com.tanfra.shopmob.features.commonScaffold.router.ScaffoldRoutes
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
    getTopLevelDestItems: (
        NavHostController,
        (String, Boolean, (@Composable () -> Unit)?) -> Unit,  // resetToScaffold
        (String, Boolean, (@Composable () -> Unit)?) -> Unit)  // setNewScaffold
    -> ImmutableList<TopLevelDestination>,
) {

    // local store
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    // setters for Scaffold parameters: title, GoBack icon, FAB
    val setNewScaffold = { daTitle: String, daFlag: Boolean, daFab: (@Composable () -> Unit)? ->
        Timber.i("MVI.UI: setting new Scaffold parameters ($daTitle, $daFlag, $daFab)")
        viewModel.process(ScaffoldAction.SetNewScaffold(daTitle, daFlag, daFab))
    }
    val restorePreviousScaffold = {
        Timber.i("MVI.UI: restoring previous Scaffold parameters")
        viewModel.process(ScaffoldAction.SetPreviousScaffold)
    }
    val resetToScaffold = { daTitle: String, daFlag: Boolean, daFab: (@Composable () -> Unit)? ->
        Timber.i("MVI.UI: resetting new Scaffold parameters to ($daTitle, $daFlag, $daFab)")
        viewModel.process(ScaffoldAction.ResetToScaffold(daTitle, daFlag, daFab))
    }
    val setNewFab = { newFab: (@Composable () -> Unit)? ->
        Timber.i("MVI.UI: setting new FAB (${newFab.toString()})")
        viewModel.process(ScaffoldAction.SetNewFab(newFab))
    }
    val setTopLevelDest = { newDest: TopLevelDestination? ->
        Timber.i("MVI.UI: setting new TopLevel destination (${newDest?.route})")
        viewModel.process(ScaffoldAction.SetNewTopLevelDest(newDest))
    }


    // navigation root
    val navController: NavHostController = rememberNavController()

    // fetch TopLevel navigation destinations
    val bottomBarDestItems = remember {
        getTopLevelDestItems(navController, resetToScaffold, setNewScaffold)
            .items.filter { dest -> dest.isBottomBar }
    }
    val drawerMenuDestItems = remember {
        getTopLevelDestItems(navController, resetToScaffold, setNewScaffold)
            .items.filter { dest -> dest.isNavDrawer }
    }


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
            viewModel.process(ScaffoldAction.SetNewTopLevelDest(drawerMenuDestItems[0]))
            setNewScaffold(startTitle, false, null)
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


    // trace re-composes
    Timber.i("MVI.UI: recomposing 'ScreenScaffold' - titleStack: ${viewState.titleStack.items}")

    // where are we now?
    val currDest = navController.currentBackStackEntryAsState().value?.destination

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    // Timber.i("MVI.UI: recomposing 'TopAppBar' title: $cachedTitle")
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = viewState.titleStack.items.last(),
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
                    if (viewState.goBackFlagStack.items.last()) {
                        IconButton(
                            modifier = Modifier,
                            onClick = {
                                restorePreviousScaffold()
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
            if(bottomBarDestItems.isNotEmpty()) {
                BottomBar(
                    destinations = ImmutableList(bottomBarDestItems),
                    currDest = currDest,
                    selTopLevelDest = viewState.selTopLevelDest,
                    setTopLevelDest = setTopLevelDest,
                )
            }  // any BottomBar destinations at all?
        },
        floatingActionButton = { viewState.fabStack.items.last()?.let { it() } },
        floatingActionButtonPosition = FabPosition.End,
    ) { paddingValues ->
        NavDrawer(
            modifier = Modifier.padding(paddingValues),
            destinations = ImmutableList(drawerMenuDestItems),
            currDest = currDest,
            selTopLevelDest = viewState.selTopLevelDest,
            setTopLevelDest = setTopLevelDest,
            drawerState = drawerState,
            coroutineScope = scope,
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                routes(
                    navController = navController,
                    setNewScaffold = setNewScaffold,
                    restorePreviousScaffold = restorePreviousScaffold,
                    setNewFab = setNewFab,
                )
            }
        }
    }
}


@Preview
@Composable
private fun ScreenScaffoldPreview() {
    MaterialTheme {
        ScaffoldScreen(
            viewModel = koinViewModel(),
            startTitle = "App",
            startDestination = "AppStartDest",
            getTopLevelDestItems = ScaffoldRoutes.ScaffoldScreen.getTopLevelDestinations,
        )
    }
}
