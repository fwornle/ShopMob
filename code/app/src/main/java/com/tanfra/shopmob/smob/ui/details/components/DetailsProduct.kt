package com.tanfra.shopmob.smob.ui.details.components

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
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.features.common.view.KeyValueText
import com.tanfra.shopmob.features.common.theme.ShopMobTheme

@Composable
fun DetailsProduct(
    modifier: Modifier = Modifier,
    item: SmobProductATO = SmobProductATO()
) {

    // component local state
    val scrollState = rememberScrollState()

    // product item
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
            text = stringResource(R.string.smob_product_details),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(40.dp))
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.smob_1),
            contentDescription = item.description,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .height(120.dp)
                .padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))

        KeyValueText(
            key = stringResource(R.string.title_product),
            value = item.name
        )

        KeyValueText(
            key = stringResource(R.string.description),
            value = item.description ?: "--"
        )

        KeyValueText(
            key = stringResource(R.string.catMain),
            value = item.category.main.toString()
        )

        KeyValueText(
            key = stringResource(R.string.catSub),
            value = item.category.sub.toString()
        )

        KeyValueText(
            key = stringResource(R.string.actDate),
            value = item.activity.date
        )

        KeyValueText(
            key = stringResource(R.string.actReps),
            value = item.activity.reps.toString()
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
        DetailsProduct()
    }

}
