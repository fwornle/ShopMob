package com.tanfra.shopmob.features.smobPlanning.presentation.view.products

import com.tanfra.shopmob.features.common.arch.Reducer
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningMutation
import com.tanfra.shopmob.features.smobPlanning.presentation.view.PlanningViewState
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO

class PlanningProductsMutationReducer : Reducer<PlanningMutation, PlanningViewState> {
    override fun invoke(mutation: PlanningMutation, currentState: PlanningViewState): PlanningViewState =
        when (mutation) {
            is PlanningMutation.ShowProductsOnList ->
                currentState.mutateToShowContent(
                    list = mutation.list,
                    productList = mutation.products
                )  // products screen
            is PlanningMutation.ShowProductDetails ->
                currentState.mutateToShowContent(product = mutation.product)  // product details
            else -> currentState // mutation not handled in this reducer --> maintain current state
        }

    // products screen - show products on selected list
    @JvmName("mutateToShowProducts")
    private fun PlanningViewState.mutateToShowContent(
        list: SmobListATO,
        productList: List<SmobProductATO>
    ) =
        copy(
            isLoaderVisible = false,
            isContentVisible = true,
            selectedList = list,
            productItemsOnList = productList,
            isErrorVisible = false,
        )

    // products details screen - show details of selected product
    @JvmName("mutateToShowProductDetails")
    private fun PlanningViewState.mutateToShowContent(product: SmobProductATO) =
        copy(
            isLoaderVisible = false,
            isContentVisible = true,
            selectedProduct = product,
            isErrorVisible = false,
        )

}