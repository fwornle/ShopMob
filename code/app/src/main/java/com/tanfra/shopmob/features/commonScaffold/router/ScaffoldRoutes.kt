package com.tanfra.shopmob.features.commonScaffold.router

import androidx.compose.runtime.Composable
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view.PlanningListsBrowseScreen
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import org.koin.androidx.compose.koinViewModel

sealed class ScaffoldRoutes {

    data object ScaffoldScreen : ScaffoldRoutes() {
        const val route = "shopMobScaffold"
        const val title = "ShopMob"

        @Composable
        fun Screen(
            navigateToList: (list: SmobListATO) -> Unit,
        ) = PlanningListsBrowseScreen(
            viewModel = koinViewModel(),
            onNavigateToList = navigateToList,
        ) { list -> list }
    }

}