package com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.obsoleteRemove

import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO

// ui state
data class PlanningListsBrowseUiState(
    val isLoaderVisible: Boolean = false,
    val isEmptyVisible: Boolean = false,
    val isListsVisible: Boolean = false,
    val lists: List<SmobListATO> = listOf(),
    val isErrorVisible: Boolean = false,
    val errorMessage: String = "",
)
