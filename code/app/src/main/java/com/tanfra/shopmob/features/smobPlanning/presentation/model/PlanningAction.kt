package com.tanfra.shopmob.features.smobPlanning.presentation.model

import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.types.InShop
import com.tanfra.shopmob.smob.data.types.ProductCategory

sealed interface PlanningAction {

    // default actions
    data object CheckConnectivity : PlanningAction
    data object IllegalSwipe : PlanningAction
    data object LoadLists : PlanningAction
    data object LoadShops : PlanningAction
    data object LoadGroups : PlanningAction
    data object RefreshLists : PlanningAction
    data object RefreshShops : PlanningAction
    data object RefreshProducts : PlanningAction

    // lists actions
    data class ConfirmListSwipe(val item: SmobListATO) : PlanningAction
    data class NavigateToProductsOnList(val list: SmobListATO) : PlanningAction
    data class SaveNewListItem(
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
    data class SaveNewProductOnListItem(
        val selectedListId: String,
        val productName: String,
        val productDescription: String,
        val productCategory: ProductCategory,
        val productInShop: InShop,
    ) : PlanningAction

    // shops actions
    data class ConfirmShopSwipe(val item: SmobShopATO) : PlanningAction
    data class LoadShop(val shopId: String) : PlanningAction
    data class NavigateToShopDetails(val shop: SmobShopATO) : PlanningAction
    data class SaveNewShopItem(
        val name: String,
        val description: String,
        val group: Pair<String, String>
    ) : PlanningAction

}