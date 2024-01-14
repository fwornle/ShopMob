package com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.view

import android.view.KeyEvent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.tanfra.shopmob.R
import com.tanfra.shopmob.features.common.view.DropDown
import com.tanfra.shopmob.features.common.theme.ShopMobTheme
import com.tanfra.shopmob.features.common.view.FabSaveNewItem
import com.tanfra.shopmob.smob.data.types.ImmutableList

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PlanningListsAddItemContent(
    groupItems: List<Pair<String, String>>,
    setFab: ((@Composable () -> Unit)?) -> Unit,
    onSaveClicked: (String, String, Pair<String, String>) -> Unit,
) {

    // local uiState
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var groupItemSelected by remember { mutableStateOf(Pair("", "")) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // derived values
    val inputValidPattern = remember { Regex("[a-zA-Z0-9äöüÄÖÜ ]+") }
    val isNameValid by remember { derivedStateOf { name.matches(inputValidPattern) } }
    val isDescriptionValid by remember { derivedStateOf { description.matches(inputValidPattern) } }


    // activate floating action button (FAB) - if save conditions have been met
    LaunchedEffect(isNameValid) {
        if(isNameValid) {
            setFab {
                FabSaveNewItem {
                    onSaveClicked(
                        name,
                        description,
                        groupItemSelected,
                    )

                    // reset inputs
                    name = ""
                    description = ""
                    groupItemSelected = Pair("", "")

                    // remove FAB
                    setFab(null)
                }
            }
        } else {
            setFab(null)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween

    ) {

        Column {

            // list name
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(id = R.string.add_smob_list_name)) },
                isError = name.isNotEmpty() && !isNameValid,
                singleLine = true,
                maxLines= 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.moveFocus(FocusDirection.Next)
                        keyboardController?.hide()
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onKeyEvent {
                        if (
//                            it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER ||
                            it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_TAB
                        ) {
                            focusManager.moveFocus(FocusDirection.Next)
                        }
                        false
                    }
            )

            // list description
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(id = R.string.add_smob_list_desc)) },
                isError = description.isNotEmpty() && !isDescriptionValid,
                singleLine = true,
                maxLines= 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.moveFocus(FocusDirection.Next)
                        keyboardController?.hide()
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onKeyEvent {
                        if (
//                            it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER ||
                            it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_TAB
                        ) {
                            focusManager.moveFocus(FocusDirection.Next)
                        }
                        false
                    }
            )

            DropDown(
                list = ImmutableList(groupItems),
                onItemSelected = { groupItemSelected = it },
                prompt = stringResource(id = R.string.add_smob_group_member_prompt),
                label = { Text(stringResource(id = R.string.add_smob_group_select)) },
            )

        }

    }  // Column

}


@Preview(
    name = "Planning Lists New Item",
    showSystemUi = true,
)
@Composable
fun PreviewPlanningListsAddItem() {

    // static parameters
    val groups = listOf(Pair("id1", "item 1"), Pair("id2", "item 2"))

    ShopMobTheme {
        PlanningListsAddItemContent(
            groupItems = groups,
            setFab = {},
            onSaveClicked = { _: String, _: String, _: Pair<String, String> -> },
        )
    }

}

