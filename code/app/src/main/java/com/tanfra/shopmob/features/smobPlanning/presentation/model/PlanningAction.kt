package com.tanfra.shopmob.features.smobPlanning.presentation.model

import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO

sealed interface PlanningAction {

    // default actions
    data object CheckConnectivity : PlanningAction
    data object IllegalSwipe : PlanningAction
    data object LoadLists : PlanningAction
    data object LoadGroups : PlanningAction
    data object RefreshLists : PlanningAction
    data object RefreshProducts : PlanningAction

    // lists actions
    data class ConfirmListSwipe(val item: SmobListATO) : PlanningAction
    data class NavigateToProductsOnList(val list: SmobListATO) : PlanningAction
    data class SaveNewItem(
        val name: String,
        val description: String,
        val group: Pair<String, String>
    ) : PlanningAction

    // products actions
    data class LoadProductsOnList(val listId: String) : PlanningAction
    data class ConfirmProductOnListSwipe(
        val list: SmobListATO,
        val product: SmobProductATO,
    ) : PlanningAction
    data class LoadProduct(val productId: String) : PlanningAction

}