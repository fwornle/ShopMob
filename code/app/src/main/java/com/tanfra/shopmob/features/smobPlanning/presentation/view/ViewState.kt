package com.tanfra.shopmob.features.smobPlanning.presentation.view

import androidx.compose.runtime.Immutable
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO

data class ViewState(
    val isConnectivityVisible: Boolean = false,
    val isLoaderVisible: Boolean = false,
    val isErrorVisible: Boolean = false,
    val errorMessage: String = "",
    val isContentVisible: Boolean = false,
    val listItems: List<SmobListATO> = listOf(),
    val groupItems: List<GroupItemState> = listOf(),
)

@Immutable
data class GroupItemState(
    val id: String = "",
    val name: String = "",
)