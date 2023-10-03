package com.tanfra.shopmob.features.smobPlanning.presentation

import androidx.compose.runtime.Immutable
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO

sealed interface ViewState {
    data object Loading : ViewState

    data class Error(
        val message: String,
    ) : ViewState

    @Immutable
    data class Content(
        val lists: List<SmobListATO> = listOf(),
    ) : ViewState
}