package com.tanfra.shopmob.features.smobPlanning.presentation.view.shops

import com.tanfra.shopmob.features.common.arch.Reducer
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningMutation
import com.tanfra.shopmob.features.smobPlanning.presentation.view.PlanningViewState
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO

class PlanningShopsMutationReducer : Reducer<PlanningMutation, PlanningViewState> {
    override fun invoke(mutation: PlanningMutation, currentState: PlanningViewState): PlanningViewState =
        when (mutation) {
            is PlanningMutation.ShowShops ->
                currentState.mutateToShowContent(
                    shopList = mutation.shops,
                )  // products screen
            is PlanningMutation.ShowShopDetails ->
                currentState.mutateToShowContent(shop = mutation.shop)  // product details
            else -> currentState // mutation not handled in this reducer --> maintain current state
        }

    // shops screen - show shops in DB
    @JvmName("mutateToShowShops")
    private fun PlanningViewState.mutateToShowContent(
        shopList: List<SmobShopATO>
    ) =
        copy(
            isLoaderVisible = false,
            isContentVisible = true,
            shopItems = shopList,
            isErrorVisible = false,
        )

    // shop details screen - show details of selected shop
    @JvmName("mutateToShowShopDetails")
    private fun PlanningViewState.mutateToShowContent(shop: SmobShopATO) =
        copy(
            isLoaderVisible = false,
            isContentVisible = true,
            selectedShop = shop,
            isErrorVisible = false,
        )

}