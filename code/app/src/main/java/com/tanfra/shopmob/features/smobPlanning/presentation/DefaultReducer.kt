package com.tanfra.shopmob.features.smobPlanning.presentation

import android.content.res.Resources
import com.tanfra.shopmob.R
import com.tanfra.shopmob.features.common.arch.Reducer
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Mutation
import com.tanfra.shopmob.features.smobPlanning.presentation.view.ViewState

class DefaultReducer(
    private val resources: Resources,
) : Reducer<Mutation, ViewState> {
    override fun invoke(mutation: Mutation, currentState: ViewState): ViewState =
        when (mutation) {
            Mutation.DismissLostConnection ->
                currentState.mutateToDismissLostConnection()
            is Mutation.ShowError ->
                currentState.mutateToShowError(exception = mutation.exception)
            Mutation.ShowLoader ->
                currentState.mutateToShowLoader()
            Mutation.ShowLostConnection ->
                currentState.mutateToShowLostConnection()
            else -> currentState // mutation not handled in this reducer --> keep current state
        }

    private fun ViewState.mutateToDismissLostConnection() =
        copy(isConnectivityVisible = false)

    private fun ViewState.mutateToShowError(exception: Exception) =
        copy(
            isLoaderVisible = false,
            isListItemsVisible = false,
            isErrorVisible = true,
            errorMessage = resources.getString(R.string.err_generic)
                .format(exception.message),
        )

    private fun ViewState.mutateToShowLostConnection() =
        copy(isConnectivityVisible = true)

    private fun ViewState.mutateToShowLoader() =
        copy(isLoaderVisible = true)

}