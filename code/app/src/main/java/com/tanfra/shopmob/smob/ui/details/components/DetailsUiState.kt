package com.tanfra.shopmob.smob.ui.details.components

import com.tanfra.shopmob.smob.data.repo.ato.Ato
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.ui.zeUiBase.NavigationSource

data class DetailsUiState(
    val isLoading: Boolean = false,
    val navSource: NavigationSource = NavigationSource.UNKNOWN,
    val item: Ato? = null,
    val sendToMap: (SmobShopATO) -> Unit = {},  // only used with SmobShop items
    val sendToShop: () -> Unit = {},  // only used with SmobShop items
)
