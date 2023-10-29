package com.tanfra.shopmob.features.smobPlanning.presentation.view.products.view

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
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO

@Composable
internal fun PlanningProductDetailsView(
    product: SmobProductATO,
) {

    // component local state
    val scrollState = rememberScrollState()

    // product item
    Column(
        modifier = Modifier
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
                .data(product.imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.smob_1),
            contentDescription = product.description,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .height(120.dp)
                .padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))

        KeyValueText(
            key = stringResource(R.string.title_product),
            value = product.name
        )

        KeyValueText(
            key = stringResource(R.string.description),
            value = product.description ?: "--"
        )

        KeyValueText(
            key = stringResource(R.string.catMain),
            value = product.category.main.toString()
        )

        KeyValueText(
            key = stringResource(R.string.catSub),
            value = product.category.sub.toString()
        )

        KeyValueText(
            key = stringResource(R.string.actDate),
            value = product.activity.date
        )

        KeyValueText(
            key = stringResource(R.string.actReps),
            value = product.activity.reps.toString()
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
        PlanningProductDetailsView(
            SmobProductATO()
        )
    }

}
