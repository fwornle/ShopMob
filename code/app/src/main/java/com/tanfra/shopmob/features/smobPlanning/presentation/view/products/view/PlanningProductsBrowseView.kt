package com.tanfra.shopmob.features.smobPlanning.presentation.view.products.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.types.ItemStatus
import kotlinx.coroutines.launch

@Composable
internal fun PlanningProductsBrowseView(
    snackbarHostState: SnackbarHostState,
    list: SmobListATO,
    products: List<SmobProductATO>,
    onSwipeActionConfirmed: (SmobListATO, SmobProductATO) -> Unit,
    onIllegalTransition: () -> Unit,
    onClickItem: (SmobProductATO) -> Unit,
) {
    LazyColumn {
        items(
            products,
        ) {
            ListItem(
                item = it,
                snackbarHostState = snackbarHostState,
                onSwipeActionConfirmed = { product: SmobProductATO ->
                    onSwipeActionConfirmed(list, product) },
                onIllegalTransition = onIllegalTransition,
                onClickItem = onClickItem,
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
        verticalAlignment = Alignment.CenterVertically,
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
    item: SmobProductATO,
    snackbarHostState: SnackbarHostState,
    onSwipeActionConfirmed: (SmobProductATO) -> Unit,
    onIllegalTransition: () -> Unit = {},
    onClickItem: (SmobProductATO) -> Unit,
) {
    var show by remember { mutableStateOf(true) }
    var undo by remember { mutableStateOf(false) }

    val currentItem by rememberUpdatedState(item)
    val undoItem by remember { mutableStateOf(SmobProductATO()) }

    val coroutineScope = rememberCoroutineScope()

    // offer possibility to 'undo' item deletion (via snackbar action)
    val onShowSnackbar: (SmobProductATO) -> Unit = { daItem ->
        coroutineScope.launch {
            val snackbarResult = snackbarHostState.showSnackbar(
                message = "Item ${daItem.name} deleted from list",
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
                    // revert status
                    when (currentItem.status) {
                        ItemStatus.DONE -> {
                            currentItem.status = ItemStatus.IN_PROGRESS
                            onSwipeActionConfirmed(currentItem)
                        }
                        ItemStatus.IN_PROGRESS -> {
                            currentItem.status = ItemStatus.OPEN
                            onSwipeActionConfirmed(currentItem)
                        }
                        ItemStatus.OPEN, ItemStatus.NEW -> {
                            // set up "undo" and mark item as deleted
                            undoItem.status = currentItem.status

                            // delete...
                            currentItem.status = ItemStatus.DELETED

                            // show "undo" snackbar and store in DB (local & backend)
                            onShowSnackbar(currentItem)

                            // indicate fading-out of item
                            show = false

                            // confirm dismissal of item
                            return@rememberDismissState true
                        }
                        // DELETED, INVALID - should never get here
                        else -> {
                            onIllegalTransition()
                        }
                    }  // when

                    // "veto" dismissal of item
                    false
                }

                DismissValue.DismissedToStart -> {
                    // advance status
                    when (currentItem.status) {
                        ItemStatus.NEW, ItemStatus.OPEN -> {
                            currentItem.status = ItemStatus.IN_PROGRESS
                            onSwipeActionConfirmed(currentItem)
                        }
                        ItemStatus.IN_PROGRESS -> {
                            currentItem.status = ItemStatus.DONE
                            onSwipeActionConfirmed(currentItem)
                        }
                        // DELETED, INVALID - should never get here
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
                PlanningProductsCardView(
                    item = item,
                    onClickItem = onClickItem,
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
