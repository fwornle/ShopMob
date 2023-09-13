package com.tanfra.shopmob.smob.ui.planning.lists.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.planning.PlanningViewModel
import com.tanfra.shopmob.smob.ui.planning.lists.PlanningListsUiState
import timber.log.Timber

@Composable
fun PlanningListsScreen(
    viewModel: PlanningViewModel,
) {


    // collect ui state flow
    val uiState: PlanningListsUiState by viewModel.uiStateListsSF.collectAsStateWithLifecycle(
        initialValue = PlanningListsUiState(isLoading = true),
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = { Timber.i("FAB pressed") }) {
                Icon(Icons.Filled.Add, stringResource(id = R.string.add_smob_item))
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = viewModel::swiperefreshItemsInLocalDB) {
                    Text("Refresh")
                }
            }

            if (uiState.lists.isEmpty()) {
                NoListsInfo()
            }
            else {
                PlanningLists(
                    lists = uiState.lists,
                )
            }
        }

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Center))
            }
        }
    }
}


@Composable
fun NoListsInfo() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
        Text(stringResource(R.string.no_data), color = Color.Gray)
    }
}
