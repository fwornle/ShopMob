package com.tanfra.shopmob.features.smobPlanning.presentation.view.shops.view

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tanfra.shopmob.R
import com.tanfra.shopmob.features.common.theme.ShopMobTheme
import com.tanfra.shopmob.features.common.view.KeyValueText
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO

@Composable
internal fun PlanningShopDetailsView(
    shop: SmobShopATO,
) {

    // component local state
    val scrollState = rememberScrollState()

    // week days
    val daWeek = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    // shop item
    Column(
        modifier = Modifier
            .fillMaxSize()
            .scrollable(
                state = scrollState,
                orientation = Orientation.Vertical
            )
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.smob_shop_details),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(40.dp))
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(shop.imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.smob_1),
            contentDescription = shop.description,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .height(120.dp)
                .padding(horizontal =12.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))

        KeyValueText(
            key = stringResource(R.string.title_shop),
            value = shop.name
        )

        KeyValueText(
            key = stringResource(R.string.description),
            value = shop.description ?: "--"
        )

        KeyValueText(
            key = stringResource(R.string.location),
            value = "${shop.location.latitude} | ${shop.location.longitude}",
            animate = true
        )

        KeyValueText(
            key = stringResource(R.string.storeType),
            value = shop.type.toString()
        )

        KeyValueText(
            key = stringResource(R.string.storeCategory),
            value = shop.category.toString()
        )

        KeyValueText(
            key = stringResource(R.string.storeBusiness),
            value = shop.business
                .mapIndexed { idx, day -> "${daWeek[idx]}: $day\n" }
                .reduce { acc, itm -> acc + itm}
        )

        Spacer(modifier = Modifier.height(40.dp))

    }  // Column

}


@Preview(
    name = "Product Details",
    showSystemUi = true,
)
@Composable
fun PreviewSmobItemDetailsProduct() {

    ShopMobTheme {
        PlanningShopDetailsView(
            SmobShopATO()
        )
    }

}
