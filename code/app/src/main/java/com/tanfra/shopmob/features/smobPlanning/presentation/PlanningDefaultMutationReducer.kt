package com.tanfra.shopmob.features.smobPlanning.presentation

import android.content.res.Resources
import com.tanfra.shopmob.R
import com.tanfra.shopmob.features.common.arch.Reducer
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningMutation
import com.tanfra.shopmob.features.smobPlanning.presentation.view.PlanningViewState

class PlanningDefaultMutationReducer(
    private val resources: Resources,
) : Reducer<PlanningMutation, PlanningViewState> {
    override fun invoke(mutation: PlanningMutation, currentState: PlanningViewState): PlanningViewState =
        when (mutation) {
            PlanningMutation.ShowLoader ->
                currentState.mutateToShowLoader()
            PlanningMutation.ShowLostConnection ->
                currentState.mutateToShowLostConnection()
            PlanningMutation.DismissLostConnection ->
                currentState.mutateToDismissLostConnection()
            is PlanningMutation.ShowError ->
                currentState.mutateToShowError(exception = mutation.exception)
            else -> currentState // mutation not handled in this reducer --> keep current state
        }

    private fun PlanningViewState.mutateToDismissLostConnection() =
        copy(isConnectivityVisible = false)

    private fun PlanningViewState.mutateToShowError(exception: Exception) =
        copy(
            isLoaderVisible = false,
            isContentVisible = false,
            isErrorVisible = true,
            errorMessage = resources.getString(R.string.err_generic)
                .format(exception.message),
        )

    private fun PlanningViewState.mutateToShowLostConnection() =
        copy(isConnectivityVisible = true)

    private fun PlanningViewState.mutateToShowLoader() =
        copy(isLoaderVisible = true)

}