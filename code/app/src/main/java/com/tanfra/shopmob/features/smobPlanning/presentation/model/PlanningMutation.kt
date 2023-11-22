package com.tanfra.shopmob.features.smobPlanning.presentation.model

import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO

sealed interface PlanningMutation {

    data object ShowLostConnection : PlanningMutation
    data object DismissLostConnection : PlanningMutation
    data object ShowLoader : PlanningMutation
    data class ShowError(val exception: Exception) : PlanningMutation

    data class ShowFormWithGroups(val groups: List<SmobGroupATO>) : PlanningMutation
    data class ShowLists(val lists: List<SmobListATO>) : PlanningMutation
    data class ShowProductsOnList(
        val list: SmobListATO,
        val products: List<SmobProductATO>
    ) : PlanningMutation
    data class ShowProductDetails(val product: SmobProductATO) : PlanningMutation

}