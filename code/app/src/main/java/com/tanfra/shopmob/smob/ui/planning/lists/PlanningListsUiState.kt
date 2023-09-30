package com.tanfra.shopmob.smob.ui.planning.lists

import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO

// ui state
data class PlanningListsUiState(
    val isLoaderVisible: Boolean = false,
    val isEmptyVisible: Boolean = false,
    val isListsVisible: Boolean = false,
    val lists: List<SmobListATO> = listOf(),
    val isErrorVisible: Boolean = false,
    val errorMessage: String = "",
)
