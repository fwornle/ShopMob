package com.tanfra.shopmob.features.commonScaffold.presentation.view

import androidx.compose.runtime.Composable
import com.tanfra.shopmob.smob.data.types.ImmutableList

data class ScaffoldViewState(

    // scaffold states
    val titleStack: ImmutableList<String> = ImmutableList(listOf("App")),
    val goBackFlagStack: ImmutableList<Boolean> = ImmutableList(listOf(false)),
    val fabStack: ImmutableList<(@Composable () -> Unit)?> = ImmutableList(listOf(null)),
    val selTopLevelDest: TopLevelDestination? = null,


    // generic content
    val isConnectivityVisible: Boolean = false,
    val isLoaderVisible: Boolean = false,
    val isErrorVisible: Boolean = false,
    val errorMessage: String = "",
    val isContentVisible: Boolean = false

)
