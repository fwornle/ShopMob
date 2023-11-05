package com.tanfra.shopmob.features.common.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.tanfra.shopmob.R

@Composable
fun FabSaveNewItem(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Default.Send,
            contentDescription = stringResource(id = R.string.save_smob_item)
        )
    }
}

