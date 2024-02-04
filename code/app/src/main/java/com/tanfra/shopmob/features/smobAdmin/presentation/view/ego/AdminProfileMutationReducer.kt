package com.tanfra.shopmob.features.smobAdmin.presentation.view.ego

import com.tanfra.shopmob.features.common.arch.Reducer
import com.tanfra.shopmob.features.smobAdmin.presentation.model.AdminMutation
import com.tanfra.shopmob.features.smobAdmin.presentation.view.AdminViewState
import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO

class AdminProfileMutationReducer : Reducer<AdminMutation, AdminViewState> {
    override fun invoke(mutation: AdminMutation, currentState: AdminViewState): AdminViewState =
        when (mutation) {
            is AdminMutation.ShowUserDetails ->
                currentState.mutateToShowContent(
                    user = mutation.user,
                )  // profile screen
            else -> currentState // mutation not handled in this reducer --> maintain current state
        }

    // user details screen - show details of current user (= profile)
    @JvmName("mutateToShowUserDetails")
    private fun AdminViewState.mutateToShowContent(user: SmobUserATO) =
        copy(
            isLoaderVisible = false,
            isContentVisible = true,
            selectedUser = user,
            isErrorVisible = false,
        )

}