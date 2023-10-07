package com.tanfra.shopmob.features.smobPlanning.presentation.view

import androidx.compose.runtime.Immutable
import com.tanfra.shopmob.smob.data.types.ItemStatus

data class ViewState(
    val isConnectivityVisible: Boolean = false,
    val isLoaderVisible: Boolean = false,
    val isItemListVisible: Boolean = false,
    val listItemList: List<ListItemState> = listOf(),
    val groupItemList: List<GroupItemState> = listOf(),
    val isErrorVisible: Boolean = false,
    val errorMessage: String = "",
)

@Immutable
data class ListItemState(
    val id: String = "",
    val status: ItemStatus,
    val position: Long,
    val name: String = "",
    val description: String = "",
)

@Immutable
data class GroupItemState(
    val id: String = "",
    val name: String = "",
)