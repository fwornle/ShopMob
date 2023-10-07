package com.tanfra.shopmob.features.smobPlanning.presentation

import android.content.res.Resources
import com.tanfra.shopmob.R
import com.tanfra.shopmob.features.common.arch.Reducer
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Mutation
import com.tanfra.shopmob.features.smobPlanning.presentation.view.ListItemState
import com.tanfra.shopmob.features.smobPlanning.presentation.view.ViewState
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import java.lang.Exception

class DefaultReducer(
    private val resources: Resources,
) : Reducer<Mutation, ViewState> {
    override fun invoke(mutation: Mutation, currentState: ViewState): ViewState =
        when (mutation) {
            Mutation.DismissLostConnection ->
                currentState.mutateToDismissLostConnection()
            is Mutation.ShowContent ->
                currentState.mutateToShowContent(items = mutation.lists)
            is Mutation.ShowError ->
                currentState.mutateToShowError(exception = mutation.exception)
            Mutation.ShowLoader ->
                currentState.mutateToShowLoader()
            Mutation.ShowLostConnection ->
                currentState.mutateToShowLostConnection()
        }

    private fun ViewState.mutateToDismissLostConnection() =
        copy(isConnectivityVisible = false)

    private fun ViewState.mutateToShowContent(items: List<SmobListATO>) =
        copy(
            isLoaderVisible = false,
            isItemListVisible = true,
            listItemList = listItemList.toMutableList().apply {
                addAll(items.map { it.toListItemState() })
            },
            isErrorVisible = false,
        )

    private fun ViewState.mutateToShowError(exception: Exception) =
        copy(
            isLoaderVisible = false,
            isItemListVisible = false,
            isErrorVisible = true,
            errorMessage = resources.getString(R.string.err_generic)
                .format(exception.message),
        )

    private fun ViewState.mutateToShowLostConnection() =
        copy(isConnectivityVisible = true)

    private fun ViewState.mutateToShowLoader() =
        copy(isLoaderVisible = true)


    // reduce data layer items to UI relevant info
    private fun SmobListATO.toListItemState()  =
        ListItemState(
            id = this.id,
            status = this.status,
            position = this.position,
            name = this.name,
            description = this.description ?: "",
        )

    private fun String.toFlag(): String {
        val flagOffset = 0x1F1E6
        val asciiOffset = 0x41

        val firstChar = Character.codePointAt(this, 0) - asciiOffset + flagOffset
        val secondChar = Character.codePointAt(this, 1) - asciiOffset + flagOffset

        return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
    }
}