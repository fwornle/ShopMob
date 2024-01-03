package com.tanfra.shopmob.features.commonScaffold.presentation

import android.content.res.Resources
import androidx.compose.runtime.Composable
import com.tanfra.shopmob.R
import com.tanfra.shopmob.features.common.arch.Reducer
import com.tanfra.shopmob.features.commonScaffold.presentation.model.ScaffoldMutation
import com.tanfra.shopmob.features.commonScaffold.presentation.view.ScaffoldViewState
import com.tanfra.shopmob.smob.data.types.ImmutableList

class ScaffoldMutationReducer(
    private val resources: Resources,
) : Reducer<ScaffoldMutation, ScaffoldViewState> {
    override fun invoke(mutation: ScaffoldMutation, currentState: ScaffoldViewState): ScaffoldViewState =
        when (mutation) {

            // Scaffold management mutations
            is ScaffoldMutation.SetGoBackFlag ->
                currentState.mutateToNewGoBackFlag(newFlag = mutation.daFlag)
            is ScaffoldMutation.SetNewFab -> currentState.mutateToNewFab(newFab = mutation.daFab)
            is ScaffoldMutation.SetNewTitle ->
                currentState.mutateToNewTitle(newTitle = mutation.daTitle)
            ScaffoldMutation.SetPreviousTitle ->
                currentState.mutateToPreviousTitle()

            // generic content mutations
            ScaffoldMutation.ShowLoader ->
                currentState.mutateToShowLoader()
            ScaffoldMutation.ShowLostConnection ->
                currentState.mutateToShowLostConnection()
            ScaffoldMutation.DismissLostConnection ->
                currentState.mutateToDismissLostConnection()
            is ScaffoldMutation.ShowError ->
                currentState.mutateToShowError(exception = mutation.exception)

        }

    private fun ScaffoldViewState.mutateToNewGoBackFlag(newFlag: Boolean) =
        copy(currentGoBackFlag = newFlag)

    private fun ScaffoldViewState.mutateToNewFab(newFab: (@Composable () -> Unit)?) =
        copy(currentFab = newFab)

    private fun ScaffoldViewState.mutateToNewTitle(newTitle: String): ScaffoldViewState {
        // fw-240103:
        // somehow, compose sees the need to recompose the screen (twice) --> screws up this
        // accumulative memory - the following "if" fixes this (workaround)
        return if(this.titleStack.items.contains(newTitle).not()) {
            val newTitleStack = titleStack.items.toMutableList()
            newTitleStack.add(newTitle)
            copy(
                titleStack = ImmutableList(newTitleStack),
            )
        } else {
            this
        }
    }

    private fun ScaffoldViewState.mutateToPreviousTitle(): ScaffoldViewState {
        val newTitleStack = titleStack.items.toMutableList()
        newTitleStack.removeLast()

        return copy(
            titleStack = ImmutableList(newTitleStack),
        )
    }

    private fun ScaffoldViewState.mutateToDismissLostConnection() =
        copy(isConnectivityVisible = false)

    private fun ScaffoldViewState.mutateToShowError(exception: Exception) =
        copy(
            isLoaderVisible = false,
            isContentVisible = false,
            isErrorVisible = true,
            errorMessage = resources.getString(R.string.err_generic)
                .format(exception.message),
        )

    private fun ScaffoldViewState.mutateToShowLostConnection() =
        copy(isConnectivityVisible = true)

    private fun ScaffoldViewState.mutateToShowLoader() =
        copy(isLoaderVisible = true)

}