package com.tanfra.shopmob.smob.ui.details

import com.tanfra.shopmob.smob.data.repo.ato.Ato
import com.tanfra.shopmob.smob.ui.zeUiBase.NavigationSource

// ui state
data class DetailsViewState(
    val isLoading: Boolean = false,
    val navSource: NavigationSource = NavigationSource.UNKNOWN,
    val item: Ato? = null
)
