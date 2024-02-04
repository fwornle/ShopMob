package com.tanfra.shopmob.features.smobAdmin.presentation.view

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Immutable
import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO

data class AdminViewState(

    // generic
    val isConnectivityVisible: Boolean = false,
    val isLoaderVisible: Boolean = false,
    val isErrorVisible: Boolean = false,
    val errorMessage: String = "",
    val isContentVisible: Boolean = false,
    val isRefreshing: Boolean = false,
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),

    // app specific
    val listItems: List<SmobListATO> = listOf(),
    val groupItems: List<GroupItemState> = listOf(),
    val selectedList: SmobListATO = SmobListATO(),
    val groupItemsOfList: List<SmobGroupATO> = listOf(),
    val selectedGroup: SmobGroupATO = SmobGroupATO(),
)

@Immutable
data class GroupItemState(
    val id: String = "",
    val name: String = "",
)