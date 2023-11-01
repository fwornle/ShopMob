package com.tanfra.shopmob.smob.data.types

import androidx.compose.runtime.Immutable

@Immutable
data class ImmutableList<T>(
    val items: List<T>
)