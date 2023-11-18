package com.tanfra.shopmob.features.smobPlanning.presentation.view

import androidx.compose.runtime.Immutable
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO

data class ViewState(

    // generic
    val isConnectivityVisible: Boolean = false,
    val isLoaderVisible: Boolean = false,
    val isErrorVisible: Boolean = false,
    val errorMessage: String = "",
    val isContentVisible: Boolean = false,

    // app specific
    val listItems: List<SmobListATO> = listOf(),
    val groupItems: List<GroupItemState> = listOf(),
    val selectedList: SmobListATO = SmobListATO(),
    val productItemsOnList: List<SmobProductATO> = listOf(),
    val selectedProduct: SmobProductATO = SmobProductATO(),
    val selectedShop: SmobShopATO = SmobShopATO(),
)

@Immutable
data class GroupItemState(
    val id: String = "",
    val name: String = "",
)