package com.tanfra.shopmob.smob.ui.planning.lists.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.ui.zeTheme.ShopMobTheme
import com.tanfra.shopmob.smob.ui.zeUtils.statusColor
import timber.log.Timber

@Composable
fun PlanningLists(
    lists: List<SmobListATO> = listOf(),
) {
    LazyColumn {
        items(lists) {
            ListItem(list = it)
        }
    }
}


@Composable
fun ListItem(
    list: SmobListATO,
) {
    Row (modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp)
        .clickable { Timber.i("Clicked on list ${list.name}") }
        //.border(BorderStroke(1.dp, Color.DarkGray), RoundedCornerShape(10.dp))
        .shadow(5.dp, RoundedCornerShape(10.dp))
        .clip(RoundedCornerShape(10.dp))
        .background(statusColor(list.status))
        .padding(10.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            Text(
                text = list.name,
                style = MaterialTheme.typography.headlineMedium,
            )

            Text(list.id)
        }

        IconButton(
            modifier = Modifier
                .align(CenterVertically)
                .width(32.dp)
                .height(32.dp),
            onClick = { Timber.i("Clicked on list icon") }
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}



@Preview(
    name = "Planning Lists",
    showSystemUi = true,
)
@Composable
fun PreviewPlanningLists() {

    val dE = SmobListATO(status = ItemStatus.IN_PROGRESS)
    val daList = listOf(dE, dE, dE, dE, dE, dE)

    ShopMobTheme {
        PlanningLists(daList)
    }

}
