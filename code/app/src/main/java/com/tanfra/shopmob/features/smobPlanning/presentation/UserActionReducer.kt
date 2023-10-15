package com.tanfra.shopmob.features.smobPlanning.presentation

import com.tanfra.shopmob.features.common.arch.Reducer
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Mutation
import com.tanfra.shopmob.features.smobPlanning.presentation.view.GroupItemState
import com.tanfra.shopmob.features.smobPlanning.presentation.view.ViewState
import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO

class UserActionReducer : Reducer<Mutation, ViewState> {
    override fun invoke(mutation: Mutation, currentState: ViewState): ViewState =
        when (mutation) {
            is Mutation.ShowContent ->
                currentState.mutateToShowContent(items = mutation.lists)
            is Mutation.ShowFormWithGroups ->
                currentState.mutateToShowFormWithGroups(items = mutation.groups)
            else -> currentState // mutation not handled in this reducer --> keep current state
        }

    private fun ViewState.mutateToShowContent(items: List<SmobListATO>) =
        copy(
            isLoaderVisible = false,
            isListItemsVisible = true,
            listItems = items,
            isErrorVisible = false,
        )

    private fun ViewState.mutateToShowFormWithGroups(items: List<SmobGroupATO>) =
        copy(
            isLoaderVisible = false,
            isListItemsVisible = false,
            groupItems = items.map { item -> GroupItemState(item.id, item.name) },
            isErrorVisible = false,
        )

}