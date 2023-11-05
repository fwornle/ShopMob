package com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.tanfra.shopmob.features.smobPlanning.presentation.PlanningViewModelMvi
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO

@Composable
fun PlanningShopsBrowseScreen(
    viewModel: PlanningViewModelMvi,
    onNavigateToShopsDetails: (SmobShopATO) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Temporary dummy screen: PlanningShopsBrowseScreen", fontSize = 20.sp)
    }
}

@Composable
fun Screen3() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Three Screen", fontSize = 20.sp)
    }
}
