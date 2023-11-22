package com.tanfra.shopmob.features.smobPlanning.presentation.view.lists

import com.tanfra.shopmob.features.common.arch.Reducer
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningMutation
import com.tanfra.shopmob.features.smobPlanning.presentation.view.GroupItemState
import com.tanfra.shopmob.features.smobPlanning.presentation.view.PlanningViewState
import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO

class PlanningListsMutationReducer : Reducer<PlanningMutation, PlanningViewState> {
    override fun invoke(mutation: PlanningMutation, currentState: PlanningViewState): PlanningViewState =
        when (mutation) {
            is PlanningMutation.ShowLists ->
                currentState.mutateToShowContent(lists = mutation.lists)  // lists screen
            is PlanningMutation.ShowFormWithGroups ->
                currentState.mutateToShowContent(groups = mutation.groups)  // add new list screen
            else -> currentState // mutation not handled in this reducer --> maintain current state
        }

    // Lists screen - show lists
    @JvmName("mutateToShowLists")
    private fun PlanningViewState.mutateToShowContent(lists: List<SmobListATO>) =
        copy(
            isLoaderVisible = false,
            isContentVisible = true,
            listItems = lists,
            isErrorVisible = false,
        )

    // add new list screen - show form (incl. groups dropdown)
    @JvmName("mutateToShowFormWithGroups")
    private fun PlanningViewState.mutateToShowContent(groups: List<SmobGroupATO>) =
        copy(
            isLoaderVisible = false,
            isContentVisible = true,
            groupItems = groups.map { item -> GroupItemState(item.id, item.name) },
            isErrorVisible = false,
        )

}