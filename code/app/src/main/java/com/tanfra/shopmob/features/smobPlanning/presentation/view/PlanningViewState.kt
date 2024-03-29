package com.tanfra.shopmob.features.smobPlanning.presentation.view

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Immutable
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO

data class PlanningViewState(

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
    val productItemsOnList: List<SmobProductATO> = listOf(),
    val selectedProduct: SmobProductATO = SmobProductATO(),
    val selectedShop: SmobShopATO = SmobShopATO(),
    val shopItems: List<SmobShopATO> = listOf(),
)

@Immutable
data class GroupItemState(
    val id: String = "",
    val name: String = "",
)