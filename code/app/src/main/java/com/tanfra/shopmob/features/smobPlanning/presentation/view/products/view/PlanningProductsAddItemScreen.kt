package com.tanfra.shopmob.features.smobPlanning.presentation.view.products.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.tanfra.shopmob.features.smobPlanning.presentation.PlanningViewModelMvi
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningAction
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningEvent
import com.tanfra.shopmob.features.smobPlanning.presentation.view.PlanningViewState
import com.tanfra.shopmob.smob.data.types.ImmutableList
import com.tanfra.shopmob.smob.data.types.InShop
import com.tanfra.shopmob.smob.data.types.ProductCategory
import com.tanfra.shopmob.smob.data.types.ProductMainCategory
import com.tanfra.shopmob.smob.data.types.ProductSubCategory
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PlanningProductsAddItemScreen(
    viewModel: PlanningViewModelMvi,
    selectedListId: String,
    navigateToShopSelect: () -> Unit,
    goBack: () -> Unit,
) {
    // lifecycle aware collection of viewState flow
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewState by viewModel.viewStateFlow
        .collectAsStateWithLifecycle(
            initialValue = PlanningViewState(),
            lifecycleOwner = lifecycleOwner,
            minActiveState = Lifecycle.State.STARTED,
            context = viewModel.viewModelScope.coroutineContext,
        )


    // actions to be triggered (once) on STARTED
    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

            // collect event flow - triggers reactions to signals from VM
            viewModel.eventFlow.collectLatest { event ->
                when (event) {
                    is PlanningEvent.NavigateBack -> goBack()
                    else -> { /* ignore */ }
                }
            }
        }
    }


    // fetch main and sub categories (onyl done once)
    val mainCategories = remember {
        ImmutableList(ProductMainCategory.entries.map { item ->
            Pair(item.ordinal.toString(), item.name) })
    }
    val subCategories = remember {
        ImmutableList(ProductSubCategory.entries.map { item ->
            Pair(item.ordinal.toString(), item.name) })
    }

    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        PlanningProductsAddItemContent(
            selectedListId = selectedListId,
            selectedShop = viewState.selectedShop,
            mainCategoryItems = mainCategories,
            subCategoryItems = subCategories,
            onSelectShopClicked = {},
            onSaveClicked = {
                    daSelectedListId: String,
                    daProductName: String,
                    daProductDescription: String,
                    daProductCategory: ProductCategory,
                    daProductInShop: InShop,
                ->
                viewModel.process(
                    PlanningAction.SaveNewProductOnListItem(
                        daSelectedListId,
                        daProductName,
                        daProductDescription,
                        daProductCategory,
                        daProductInShop,
                    )
                )
            },
        )
    }  // Scaffold

}
