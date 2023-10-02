package com.tanfra.shopmob.smob.ui.planning.lists

// ui state
data class PlanningListsAddItemUiState(
    val isLoaderVisible: Boolean = false,
    val isEmptyVisible: Boolean = false,
    val isErrorVisible: Boolean = false,
    val errorMessage: String = "",
    val groupItems: List<Pair<String, String>> = listOf(),
    val onSaveClicked: () -> Unit = {},
)
