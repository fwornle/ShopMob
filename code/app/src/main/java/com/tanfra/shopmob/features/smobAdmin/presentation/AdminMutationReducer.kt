package com.tanfra.shopmob.features.smobAdmin.presentation

import android.content.res.Resources
import com.tanfra.shopmob.R
import com.tanfra.shopmob.features.common.arch.Reducer
import com.tanfra.shopmob.features.smobAdmin.presentation.model.AdminMutation
import com.tanfra.shopmob.features.smobAdmin.presentation.view.AdminViewState

class AdminMutationReducer(
    private val resources: Resources,
) : Reducer<AdminMutation, AdminViewState> {
    override fun invoke(mutation: AdminMutation, currentState: AdminViewState): AdminViewState =
        when (mutation) {
            AdminMutation.ShowLoader ->
                currentState.mutateToShowLoader()
            AdminMutation.ShowLostConnection ->
                currentState.mutateToShowLostConnection()
            AdminMutation.DismissLostConnection ->
                currentState.mutateToDismissLostConnection()
            is AdminMutation.ShowError ->
                currentState.mutateToShowError(exception = mutation.exception)
            else -> currentState // mutation not handled in this reducer --> keep current state
        }

    private fun AdminViewState.mutateToDismissLostConnection() =
        copy(isConnectivityVisible = false)

    private fun AdminViewState.mutateToShowError(exception: Exception) =
        copy(
            isLoaderVisible = false,
            isContentVisible = false,
            isErrorVisible = true,
            errorMessage = resources.getString(R.string.err_generic)
                .format(exception.message),
        )

    private fun AdminViewState.mutateToShowLostConnection() =
        copy(isConnectivityVisible = true)

    private fun AdminViewState.mutateToShowLoader() =
        copy(isLoaderVisible = true)

}