package com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tanfra.shopmob.features.common.theme.colorAccent
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.ui.zeUtils.statusColor
import timber.log.Timber

@Composable
fun PlanningListsCardView(
    item: SmobListATO,
    onClickItem: (SmobListATO) -> Unit,
) {
    Row (modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp)
        .clickable { onClickItem(item) }
        //.border(BorderStroke(1.dp, Color.DarkGray), RoundedCornerShape(10.dp))
        .shadow(5.dp, RoundedCornerShape(10.dp))
        .clip(RoundedCornerShape(10.dp))
        .background(statusColor(item.status))
        .padding(10.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            androidx.compose.material3.Text(
                text = item.name,
                style = MaterialTheme.typography.headlineMedium,
            )
            androidx.compose.material3.Text(
                text = item.description ?: "(no description)",
            )
            androidx.compose.material3.Text(
                text = item.status.toString(),
                color = colorAccent,
            )
        }

        IconButton(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .width(32.dp)
                .height(32.dp),
            onClick = { Timber.i("Clicked on list icon") }
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Shopping List"
            )
        }
    }
}


@Preview
@Composable
private fun UserCarViewPreview() =
    MaterialTheme {
        PlanningListsCardView(
            item = SmobListATO(),
            onClickItem = {},
        )
    }
