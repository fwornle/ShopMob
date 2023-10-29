package com.tanfra.shopmob.features.smobPlanning.presentation.model

import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO

sealed interface Action {

    // default actions
    data object CheckConnectivity : Action
    data object IllegalSwipe : Action
    data object LoadLists : Action
    data object LoadGroups : Action
    data object RefreshLists : Action
    data object RefreshProducts : Action

    // lists actions
    data class ConfirmListSwipe(val item: SmobListATO) : Action
    data class NavigateToProductsOnList(val list: SmobListATO) : Action
    data class SaveNewItem(
        val name: String,
        val description: String,
        val group: Pair<String, String>
    ) : Action

    // products actions
    data class LoadProductsOnList(val listId: String) : Action
    data class ConfirmProductOnListSwipe(
        val list: SmobListATO,
        val product: SmobProductATO,
    ) : Action
    data class LoadProduct(val productId: String) : Action

}