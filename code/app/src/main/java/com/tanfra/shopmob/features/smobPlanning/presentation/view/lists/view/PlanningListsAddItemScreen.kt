package com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tanfra.shopmob.features.smobPlanning.presentation.PlanningViewModel
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.PlanningListsAddItemUiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlanningListsAddItemScreen(
    onNavigateBack: () -> Unit,
) {
    // inject VM
    val viewModel: PlanningViewModel = koinViewModel()

    // collect ui state flow
    val uiState by viewModel.uiStateListsAddItem.collectAsStateWithLifecycle(
        initialValue = PlanningListsAddItemUiState(),
    )

    // state of snackbar host
    val snackbarHostState = remember { SnackbarHostState() }

    Box(
        Modifier
            .fillMaxSize()
    ) {

        with(uiState) {

            Column(
                modifier = Modifier
            ) {
                PlanningListsAddItem(
                    groupItems = groupItems,
                    onSaveClicked = {
                            daName: String,
                            daDescription: String,
                            daSelection: Pair<String, String>,
                        ->
                        viewModel.saveNewSmobList(
                            daName,
                            daDescription,
                            daSelection,
                            onNavigateBack
                        )
                    },
                )
            }

            // or a loader
            if (uiState.isLoaderVisible) {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.align(Center))
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 60.dp)  // move above FABs
            )

        }  // with (uiState)

    }  // Box

}
