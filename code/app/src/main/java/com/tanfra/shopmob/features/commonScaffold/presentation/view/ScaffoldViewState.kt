package com.tanfra.shopmob.features.commonScaffold.presentation.view

import androidx.compose.runtime.Composable

data class ScaffoldViewState(

    // scaffold states
    val currentTitle: String = "App",
    val previousTitle: List<String> = listOf(),
    val currentGoBackFlag: Boolean = false,
    val currentFab: (@Composable () -> Unit)? = null,

    // generic content
    val isConnectivityVisible: Boolean = false,
    val isLoaderVisible: Boolean = false,
    val isErrorVisible: Boolean = false,
    val errorMessage: String = "",
    val isContentVisible: Boolean = false,

)
