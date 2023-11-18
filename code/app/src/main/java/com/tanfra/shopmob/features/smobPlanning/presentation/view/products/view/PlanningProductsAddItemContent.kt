package com.tanfra.shopmob.features.smobPlanning.presentation.view.products.view

import android.view.KeyEvent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanfra.shopmob.R
import com.tanfra.shopmob.app.Constants.INVALID_ITEM_ID
import com.tanfra.shopmob.features.common.view.DropDown
import com.tanfra.shopmob.features.common.theme.ShopMobTheme
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.types.ImmutableList

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PlanningProductsAddItemContent(
    selectedShop: SmobShopATO,
    mainCategoryItems: ImmutableList<Pair<String, String>>,
    subCategoryItems: ImmutableList<Pair<String, String>>,
    onSelectShopClicked: () -> Unit,
    onSaveClicked: (String, String, Pair<String, String>) -> Unit,
) {

    // local uiState
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var mainCategorySelected by remember { mutableStateOf(Pair("", "")) }
    var subCategorySelected by remember { mutableStateOf(Pair("", "")) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // derived values
    val inputValidPattern = remember { Regex("[a-zA-Z0-9äöüÄÖÜ ]+") }
    val isNameValid = name.matches(inputValidPattern)
    val isDescriptionValid = description.matches(inputValidPattern)

    // valid shop?
    val selectedShopValid = selectedShop.id != INVALID_ITEM_ID

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween

    ) {

        Column {

            // product name
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(id = R.string.add_smob_item_name)) },
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

            // product description
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(id = R.string.add_smob_item_desc)) },
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
                list = mainCategoryItems,
                onItemSelected = { mainCategorySelected = it },
                prompt = stringResource(id = R.string.smob_item_mainCat_prompt),
                label = { Text(stringResource(id = R.string.smob_item_mainCat)) }
            )

            DropDown(
                list = subCategoryItems,
                onItemSelected = { subCategorySelected = it },
                prompt = stringResource(id = R.string.smob_item_subCat_prompt),
                label = { Text(stringResource(id = R.string.smob_item_subCat))}
            )

            // product shop / category
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectShopClicked()  },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(Modifier.weight(2f)) {
                    Icon(
                        modifier = Modifier
                            .weight(1f)
                            .size(20.dp)
                            .align(Alignment.CenterVertically),
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = stringResource(id = R.string.icon_shopping_cart),
                    )
                    Text(
                        modifier = Modifier
                            .weight(3f),
                        text = if(selectedShopValid) { selectedShop.name }
                                else { stringResource(id = R.string.smob_shop_prompt) },
                        textAlign = TextAlign.Start,
                        color = Color.Blue,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Text(
                    modifier = Modifier
                        .weight(2f)
                        .padding(horizontal = 12.dp),
                    text = if(selectedShopValid) { selectedShop.category.toString() }
                            else { "(${stringResource(id = R.string.smob_shop_category)})" },
                    textAlign = TextAlign.End,
                    color = Color.Blue,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

        }


        if(name.isNotEmpty() && isNameValid) {
            Button(
                onClick = {
                    // store list
                    onSaveClicked(
                        name,
                        description,
                        mainCategorySelected,
                    )

                    // reset inputs
                    name = ""
                    description = ""
                    mainCategorySelected = Pair("", "")
                },
                contentPadding = PaddingValues(horizontal = 80.dp, vertical = 5.dp),
                modifier = Modifier
                    .padding(vertical = 2.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.save_smob_item),
                    fontSize = 16.sp
                )
            }
        }

    }  // Column

}


@Preview(
    name = "Planning Products New Item",
    showSystemUi = true,
)
@Composable
fun PreviewPlanningProductsAddItem() {

    // static parameters
    val mainCategory = ImmutableList(
        listOf(
            Pair("mainCat1", "main 1"),
            Pair("mainCat2", "main 2"),
        )
    )

    val subCategory = ImmutableList(
        listOf(
            Pair("subCat1", "sub 1"),
            Pair("subCat2", "sub 2"),
        )
    )

    ShopMobTheme {
        PlanningProductsAddItemContent(
            selectedShop = SmobShopATO(),
            mainCategoryItems = mainCategory,
            subCategoryItems = subCategory,
            onSelectShopClicked = {},
            onSaveClicked = { _: String, _: String, _: Pair<String, String> -> },
        )
    }

}

