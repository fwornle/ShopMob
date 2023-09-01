package com.tanfra.shopmob.smob.ui.details.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tanfra.shopmob.smob.data.repo.ato.Ato
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.ui.theme.ShopMobTheme
import timber.log.Timber

@Composable
fun SmobItemDetails(
    modifier: Modifier = Modifier,
    item: Ato? = null,
    cbMap: () -> Unit = { Timber.i("SmobItemDetails 'shop coordinates' clicked") },
    cbShop: () -> Unit = { Timber.i("SmobItemDetails 'send to shop' button clicked") },
) {

    // display item specific content
    when(item) {

        is SmobProductATO -> {
            SmobItemDetailsProduct(
                modifier = modifier,
                item = item
            )
        }

        is SmobShopATO -> {
            SmobItemDetailsShop(
                modifier = modifier,
                item = item,
                sendToMap = cbMap,  // fragment based navigation to GMaps activity (via intent)
                sendToShop = cbShop,  // fragment based navigation to shop activity (via intent)
            )
        }

        // item not set (or unknown type)
        else -> {
                Text("Item not set - nothing to be displayed")
        }

    }

}


@Preview(
    name = "Smob Item Details",
    showSystemUi = true,
)
@Composable
fun PreviewSmobItemDetails() {

    ShopMobTheme {
        SmobItemDetails(item = SmobProductATO())  // default (invalid) product
    }

}
