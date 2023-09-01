package com.tanfra.shopmob.smob.ui.details.components

import com.tanfra.shopmob.smob.data.repo.ato.Ato
import com.tanfra.shopmob.smob.ui.details.SmobDetailsNavSources

// ui state
data class SmobDetailsViewState(
    val isLoading: Boolean = false,
    val navSource: SmobDetailsNavSources = SmobDetailsNavSources.UNKNOWN,
    val item: Ato? = null
)
