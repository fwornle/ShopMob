package com.tanfra.shopmob.smob.ui.zeComponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.tanfra.shopmob.R

@Composable
fun NoListItemsInfo() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Icon(
            painter = painterResource(id = R.drawable.ic_no_data),
            contentDescription = "No Data"
        )
        Text(
            text = stringResource(R.string.no_data),
            color = Color.Gray,
            fontSize = 16.sp,
        )
    }
}