package com.tanfra.shopmob.smob.ui.planning.lists

import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO

// ui state
data class PlanningListsUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val lists: List<SmobListATO> = listOf(),
    val currListId: String = "invalid-list-id",
    val navSource: String = "navDrawer"
)
