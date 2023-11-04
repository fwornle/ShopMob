package com.tanfra.shopmob.features.common.view

import android.view.KeyEvent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tanfra.shopmob.features.common.theme.ShopMobTheme
import com.tanfra.shopmob.smob.data.types.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDown(
    list: ImmutableList<Pair<String, String>>,
    onItemSelected: (Pair<String, String>) -> Unit,
    prompt: String,
    label: @Composable (() -> Unit)?,
) {

    // local UI state
    var isExpanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(Pair("", "")) }

    val focusManager = LocalFocusManager.current

    Column {

        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = it },
        ) {

            TextField(
                value = selectedItem.second,
                onValueChange = { selectedItem =
                    list.items.find { item -> item.second == it } ?: Pair("invalid-id", "invalid-name")
                },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                },
                label = label,
                placeholder = {
                    Text(text = prompt)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                singleLine = true,
                maxLines= 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.moveFocus(FocusDirection.Next) }
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .onKeyEvent {
                        if (
                            it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER ||
                            it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_TAB
                        ) {
                            focusManager.moveFocus(FocusDirection.Next)
                        }
                        false
                    }
            )

            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = {
                    isExpanded = false
                }
            ) {
                Column {
                    list.items.forEach { item ->
                        DropdownMenuItem(
                            text = {
                                Text(text = item.second)  // group name
                            },
                            onClick = {
                                selectedItem = item
                                isExpanded = false

                                // store in (hoisted) uiState
                                onItemSelected(selectedItem)
                            }
                        )
                    }
                }  // Column (around drop down items)
            }

        }

    }  // Column

}


@Preview(
    name = "DropDown menu",
    showSystemUi = true,
)
@Composable
fun PreviewDropDown() {

    val selectionList = ImmutableList(
        listOf(
            Pair("id1", "first item"),
            Pair("id2", "second item"),
            Pair("id3", "third item"),
        )
    )
    val chosenItem by remember { mutableStateOf(Pair("", "")) }

    ShopMobTheme {
        Column {
            Text("Test test test")
            Spacer(modifier = Modifier.height(16.dp))

            DropDown(
                list = selectionList,
                onItemSelected = {},
                prompt = "Make your move...",
                label = { Text("Select item") },
            )

            Text("id: $chosenItem.first, name: $chosenItem.second")
        }
    }

}