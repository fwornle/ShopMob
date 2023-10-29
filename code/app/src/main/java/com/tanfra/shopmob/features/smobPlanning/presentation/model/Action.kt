package com.tanfra.shopmob.features.smobPlanning.presentation.model

import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO

sealed interface Action {
    data object CheckConnectivity : Action
    data object LoadLists : Action
    data object ReloadLists : Action
    data object LoadGroups : Action
    data class ConfirmListSwipe(val item: SmobListATO) : Action
    data class ConfirmProductOnListSwipe(
        val list: SmobListATO,
        val product: SmobProductATO,
    ) : Action
    data class NavigateToProductsOnList(val list: SmobListATO) : Action
    data class LoadProductList(val listId: String) : Action
    data class SaveNewItem(
        val name: String,
        val description: String,
        val group: Pair<String, String>
    ) : Action
    data object IllegalSwipe : Action
}