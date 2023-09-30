package com.tanfra.shopmob.smob.ui.planning.lists.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.ui.zeTheme.ShopMobTheme
import com.tanfra.shopmob.smob.ui.zeTheme.colorAccent
import com.tanfra.shopmob.smob.ui.zeUtils.statusColor
import kotlinx.coroutines.launch
import timber.log.Timber

// the list
@Composable
fun PlanningLists(
    lists: List<SmobListATO> = listOf(),
    listFilter: (List<SmobListATO>) -> List<SmobListATO> = { lists },
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    onSwipeActionConfirmed: (SmobListATO) -> Unit = { Timber.i("Confirmed action")},
    onIllegalTransition: () -> Unit = { Timber.i("Illegal swipe action triggered")},
    onClick: (SmobListATO) -> Unit = { item -> Timber.i("Clicked on item ${item.name}") }
) {

    LazyColumn {
        items(
            listFilter(lists),
        ) {
            ListItem(
                item = it,
                snackbarHostState = snackbarHostState,
                onSwipeActionConfirmed = onSwipeActionConfirmed,
                onIllegalTransition = onIllegalTransition,
                onClick = onClick,
            )
        }
    }
}

// the display item (in the list)
@Composable
fun ListItemCard(
    item: SmobListATO,
    onClick: (SmobListATO) -> Unit,
) {
    Row (modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp)
        .clickable { onClick(item) }
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
            Text(
                text = item.name,
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = item.description ?: "(no description)",
            )
            Text(
                text = item.status.toString(),
                color = colorAccent,
            )
        }

        IconButton(
            modifier = Modifier
                .align(CenterVertically)
                .width(32.dp)
                .height(32.dp),
            onClick = { Timber.i("Clicked on list icon") }
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Shopping List"
            )
        }
    }
}


/*
 * swipe to dismiss (material3)
 * ref: https://www.geeksforgeeks.org/android-jetpack-compose-swipe-to-dismiss-with-material-3/
 */

// background
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissBackground(dismissState: DismissState) {
    val color = when (dismissState.dismissDirection) {
        DismissDirection.StartToEnd -> Color(0xFFFF1744)
        DismissDirection.EndToStart -> Color(0xFF1DE9B6)
        null -> Color.Transparent
    }
    val direction = dismissState.dismissDirection

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(12.dp, 8.dp),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (direction == DismissDirection.StartToEnd) Icon(
            Icons.Default.Delete,
            contentDescription = "delete"
        )
        Spacer(modifier = Modifier)
        if (direction == DismissDirection.EndToStart) Icon(
            painter = painterResource(R.drawable.ic_baseline_done_24),
            contentDescription = "check"
        )
    }
}

// surround each ListItemCard item with swipe functionality wrapper
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListItem(
    item: SmobListATO,
    snackbarHostState: SnackbarHostState,
    onSwipeActionConfirmed: (SmobListATO) -> Unit,
    onIllegalTransition: () -> Unit = {},
    onClick: (SmobListATO) -> Unit,
) {
    var show by remember { mutableStateOf(true) }
    var undo by remember { mutableStateOf(false) }

    val currentItem by rememberUpdatedState(item)
    val undoItem by remember { mutableStateOf(SmobListATO())}

    val coroutineScope = rememberCoroutineScope()

    // offer possibility to 'undo' item deletion (via snackbar action)
    val onShowSnackbar: (SmobListATO) -> Unit = { daItem ->
        coroutineScope.launch {
            val snackbarResult = snackbarHostState.showSnackbar(
                message = "List ${daItem.name} deleted",
                actionLabel = "UNDO",
                duration = SnackbarDuration.Short
            )
            when (snackbarResult) {
                SnackbarResult.Dismissed -> onSwipeActionConfirmed(daItem)
                SnackbarResult.ActionPerformed -> {
                    // revert deleting status change (undo)
                    daItem.status = undoItem.status

                    // activate "undo" side effect (see below, LaunchedEffect)
                    undo = true
                }
            }
        }
    }

    val dismissState = rememberDismissState(
        confirmValueChange = {
            when (it) {

                DismissValue.DismissedToEnd -> {
                    // set up "undo" and mark item as deleted
                    undoItem.status = currentItem.status
                    currentItem.status = ItemStatus.DELETED

                    // show "undo" snackbar and store in DB (local & backend)
                    onShowSnackbar(currentItem)

                    // indicate fading-out of item
                    show = false

                    // confirm dismissal of item
                    true
                }

                DismissValue.DismissedToStart -> {
                    // mark all items on smobList as 'IN_PROGRESS'
                    when (currentItem.status) {
                        // ... only relevant on NEW/OPEN lists
                        ItemStatus.NEW, ItemStatus.OPEN -> {
                            currentItem.items.map { itm -> itm.status= ItemStatus.IN_PROGRESS }
                            onSwipeActionConfirmed(currentItem)
                        }
                        // smobList already is "IN_PROGRESS" or "DONE" --> indicate haptically
                        else -> {
                            onIllegalTransition()
                        }
                    }  // when

                    // "veto" dismissal of item
                    false
                }

                else -> {
                    // veto any unknown dismissals (should never happen)
                    false
                }

            }  // when (DismissValue)
        },
        positionalThreshold = { 150.dp.toPx() },
    )

    AnimatedVisibility(
        visible = show,
        exit = fadeOut(spring()),
    ) {
        SwipeToDismiss(
            state = dismissState,
            modifier = Modifier,
            background = {
                DismissBackground(dismissState)
            },
            dismissContent = {
                ListItemCard(
                    item = item,
                    onClick = onClick,
                )
            }
        )
    }

    // side effect
    LaunchedEffect(undo) {
        if (undo) {
            // user wishes to undo item deletion
            dismissState.reset()
            show = true
            undo = false
        }
    }

}


@Preview(
    name = "Planning Lists",
    showSystemUi = true,
)
@Composable
fun PreviewPlanningLists() {

    val dE1 = SmobListATO(status = ItemStatus.IN_PROGRESS)
    val dE2 = SmobListATO(status = ItemStatus.DONE)
    val daList = listOf(dE1, dE1, dE1, dE2, dE1, dE2, dE1, dE1, dE2, dE1, dE2, dE1)

    ShopMobTheme {
        PlanningLists(daList)
    }

}

