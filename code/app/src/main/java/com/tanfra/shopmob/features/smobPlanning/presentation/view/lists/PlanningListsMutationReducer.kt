package com.tanfra.shopmob.features.smobPlanning.presentation.view.lists

import com.tanfra.shopmob.features.common.arch.Reducer
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Mutation
import com.tanfra.shopmob.features.smobPlanning.presentation.view.GroupItemState
import com.tanfra.shopmob.features.smobPlanning.presentation.view.ViewState
import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO

class PlanningListsMutationReducer : Reducer<Mutation, ViewState> {
    override fun invoke(mutation: Mutation, currentState: ViewState): ViewState =
        when (mutation) {
            is Mutation.ShowLists ->
                currentState.mutateToShowContent(lists = mutation.lists)  // lists screen
            is Mutation.ShowFormWithGroups ->
                currentState.mutateToShowContent(groups = mutation.groups)  // add new list screen
            else -> currentState // mutation not handled in this reducer --> maintain current state
        }

    // Lists screen - show lists
    @JvmName("mutateToShowLists")
    private fun ViewState.mutateToShowContent(lists: List<SmobListATO>) =
        copy(
            isLoaderVisible = false,
            isContentVisible = true,
            listItems = lists,
            isErrorVisible = false,
        )

    // add new list screen - show form (incl. groups dropdown)
    @JvmName("mutateToShowFormWithGroups")
    private fun ViewState.mutateToShowContent(groups: List<SmobGroupATO>) =
        copy(
            isLoaderVisible = false,
            isContentVisible = true,
            groupItems = groups.map { item -> GroupItemState(item.id, item.name) },
            isErrorVisible = false,
        )

}