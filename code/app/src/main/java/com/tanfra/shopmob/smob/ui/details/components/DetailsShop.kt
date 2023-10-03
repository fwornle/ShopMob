package com.tanfra.shopmob.smob.ui.details.components

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.features.common.view.CrossFaderButton
import com.tanfra.shopmob.features.common.view.KeyValueText
import com.tanfra.shopmob.features.common.theme.ShopMobTheme
import timber.log.Timber

@Composable
fun DetailsShop(
    modifier: Modifier = Modifier,
    item: SmobShopATO = SmobShopATO(),
    sendToMap: (SmobShopATO) -> Unit = { Timber.i("Send user to shop location on map")},
    sendToShop: () -> Unit = { Timber.i("Send user to shop")}
) {

    // component local state
    val scrollState = rememberScrollState()

    // shop item
    Column(
        modifier = modifier
            .fillMaxSize()
            .scrollable(
                state = scrollState,
                orientation = Orientation.Vertical
            ),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
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
                .data(item.imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.smob_2),
            contentDescription = item.description,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .height(120.dp)
                .padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))

        KeyValueText(
            key = stringResource(R.string.title_shop),
            value = item.name
        )

        KeyValueText(
            key = stringResource(R.string.description),
            value = item.description ?: "--"
        )

        KeyValueText(
            modifier = Modifier.clickable { sendToMap(item) }, // dereference item clickable is "Void -> Unit"
            key = stringResource(R.string.location),
            value = "%.6f,%.6f".format(
                    item.location.latitude,
                    item.location.longitude,
                ),
            valueColor = MaterialTheme.colorScheme.primary,
            animate = true
        )

        KeyValueText(
            key = stringResource(R.string.storeType),
            value = item.type.toString()
        )

        KeyValueText(
            key = stringResource(R.string.storeCategory),
            value = item.category.toString()
        )

        KeyValueText(
            key = stringResource(R.string.storeBusiness),
            value = "Mon: %s // Tue: %s\nWed: %s // Thur: %s\nFri: %s // Sat: %s\nSun: %s"
                .format(
                    item.business[0],
                    item.business[1],
                    item.business[2],
                    item.business[3],
                    item.business[4],
                    item.business[5],
                    item.business[6]
                )
        )

        Spacer(modifier = Modifier.height(20.dp))
        CrossFaderButton(
            Modifier.height(60.dp),
            "Enter ${item.name}",
            colorResource(R.color.secondaryColor),
            colorResource(R.color.secondaryDarkColor),
            onClick = sendToShop,
        )
        Spacer(modifier = Modifier.height(20.dp))

    }  // Column

}


@Preview(
    name = "Shop Details",
    showSystemUi = true,
)
@Composable
fun PreviewSmobItemDetailsShop() {

    ShopMobTheme {
        DetailsShop()
    }

}
