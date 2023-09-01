package com.tanfra.shopmob.smob.ui.details

import com.tanfra.shopmob.smob.data.repo.ato.Ato

// ui state
data class SmobDetailsViewState(
    val isLoading: Boolean = false,
    val navSource: SmobDetailsNavSources = SmobDetailsNavSources.UNKNOWN,
    val item: Ato? = null
)
