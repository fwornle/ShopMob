package com.tanfra.shopmob.features.smobPlanning.presentation.model

import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO

sealed interface Mutation {
    data object ShowLostConnection : Mutation
    data object DismissLostConnection : Mutation
    data object ShowLoader : Mutation
    data class ShowFormWithGroups(val groups: List<SmobGroupATO>) : Mutation
    data class ShowContent(val lists: List<SmobListATO>) : Mutation
    data class ShowError(val exception: Exception) : Mutation
}