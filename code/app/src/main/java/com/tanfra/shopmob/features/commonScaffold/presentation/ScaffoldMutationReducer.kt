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
            is ScaffoldMutation.SetNewScaffold ->
                currentState.mutateToNewScaffold(
                    newTitle = mutation.daTitle,
                    newGoBackFlag = mutation.daFlag,
                    newFab = mutation.daFab
                    )
            ScaffoldMutation.SetPreviousScaffold ->
                currentState.mutateToPreviousScaffold()
            is ScaffoldMutation.ResetToScaffold ->
                currentState.mutateToResetScaffold(
                    newTitle = mutation.daTitle,
                    newGoBackFlag = mutation.daFlag,
                    newFab = mutation.daFab
                )

            is ScaffoldMutation.SetNewFab ->
                currentState.mutateToNewFab(
                    newFab = mutation.daFab
                )

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

    private fun ScaffoldViewState.mutateToNewScaffold(
        newTitle: String,
        newGoBackFlag: Boolean,
        newFab: (@Composable () -> Unit)?
    ): ScaffoldViewState {
        // fw-240103:
        // somehow, compose sees the need to recompose the screen (twice) --> screws up this
        // accumulative memory - the following "if" fixes this (workaround)
        return if(this.titleStack.items.contains(newTitle).not()) {

            val newTitleStack = titleStack.items.toMutableList()
            newTitleStack.add(newTitle)

            val newGoBackFlagStack = goBackFlagStack.items.toMutableList()
            newGoBackFlagStack.add(newGoBackFlag)

            val newFabStack = fabStack.items.toMutableList()
            newFabStack.add(newFab)

            copy(
                titleStack = ImmutableList(newTitleStack),
                goBackFlagStack = ImmutableList(newGoBackFlagStack),
                fabStack = ImmutableList(newFabStack),
            )

        } else {
            this
        }
    }

    private fun ScaffoldViewState.mutateToPreviousScaffold(): ScaffoldViewState {

        val newTitleStack = titleStack.items.toMutableList()
        newTitleStack.removeLast()

        val newGoBackFlagStack = goBackFlagStack.items.toMutableList()
        newGoBackFlagStack.removeLast()

        val newFabStack = fabStack.items.toMutableList()
        newFabStack.removeLast()

        return copy(
            titleStack = ImmutableList(newTitleStack),
            goBackFlagStack = ImmutableList(newGoBackFlagStack),
            fabStack = ImmutableList(newFabStack),
        )
    }

    private fun ScaffoldViewState.mutateToResetScaffold(
        newTitle: String,
        newGoBackFlag: Boolean,
        newFab: (@Composable () -> Unit)?
    ): ScaffoldViewState =
        copy(
            titleStack = ImmutableList(listOf(newTitle)),
            goBackFlagStack = ImmutableList(listOf(newGoBackFlag)),
            fabStack = ImmutableList(listOf(newFab)),
        )

    private fun ScaffoldViewState.mutateToNewFab(
        newFab: (@Composable () -> Unit)?
    ): ScaffoldViewState {

        // short circuit if no FAB has been set (safety - should never happen)
        if(fabStack.items.isEmpty()) return this

        // guaranteed to have at least one entry in fabStack.items - check if NOP call
        if(fabStack.items.last() == newFab) return this

        // actually swap out last FAB to new FAB
        val newFabStack = fabStack.items.toMutableList()
        newFabStack.removeLast()
        newFabStack.add(newFab)

        return copy(
            fabStack = ImmutableList(newFabStack),
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