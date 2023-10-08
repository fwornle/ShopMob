package com.tanfra.shopmob.features.smobPlanning.presentation.view

import androidx.compose.runtime.Immutable
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO

data class ViewState(
    val isConnectivityVisible: Boolean = false,
    val isLoaderVisible: Boolean = false,
    val isListItemsVisible: Boolean = false,
    val listItems: List<SmobListATO> = listOf(),
    val groupItems: List<GroupItemState> = listOf(),
    val isErrorVisible: Boolean = false,
    val errorMessage: String = "",
)

@Immutable
data class GroupItemState(
    val id: String = "",
    val name: String = "",
)