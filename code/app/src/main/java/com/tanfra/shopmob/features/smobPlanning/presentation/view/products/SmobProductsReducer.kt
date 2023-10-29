package com.tanfra.shopmob.features.smobPlanning.presentation.view.products

import com.tanfra.shopmob.features.common.arch.Reducer
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Mutation
import com.tanfra.shopmob.features.smobPlanning.presentation.view.ViewState
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO

class SmobProductsReducer : Reducer<Mutation, ViewState> {
    override fun invoke(mutation: Mutation, currentState: ViewState): ViewState =
        when (mutation) {
            is Mutation.ShowProductsOnList ->
                currentState.mutateToShowContent(
                    list = mutation.list,
                    productList = mutation.products
                )  // products screen
            is Mutation.ShowProductDetails ->
                currentState.mutateToShowContent(product = mutation.product)  // product details
            else -> currentState // mutation not handled in this reducer --> maintain current state
        }

    // products screen - show products on selected list
    @JvmName("mutateToShowProducts")
    private fun ViewState.mutateToShowContent(
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
    private fun ViewState.mutateToShowContent(product: SmobProductATO) =
        copy(
            isLoaderVisible = false,
            isContentVisible = true,
            selectedProduct = product,
            isErrorVisible = false,
        )

}