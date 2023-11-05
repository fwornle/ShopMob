package com.tanfra.shopmob.features.common.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.tanfra.shopmob.R

@Composable
fun FabAddNewItem(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(id = R.string.add_smob_item)
        )
    }
}

