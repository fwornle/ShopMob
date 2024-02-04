package com.tanfra.shopmob.features.smobAdmin.presentation.view.ego.view

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tanfra.shopmob.R
import com.tanfra.shopmob.features.common.theme.ShopMobTheme
import com.tanfra.shopmob.features.common.view.KeyValueText
import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO

@Composable
internal fun AdminProfileDetailsView(
    user: SmobUserATO,
    goBack: () -> Unit = {},
) {

    // component local state
    val scrollState = rememberScrollState()

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
            text = user.name,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(40.dp))
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(user.imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.smob_1),
            contentDescription = stringResource(R.string.profile),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .height(220.dp)
                .width(320.dp)
                .padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))

        KeyValueText(
            key = stringResource(R.string.admin_group_member_username),
            value = user.username
        )

        KeyValueText(
            key = stringResource(R.string.admin_group_member_name),
            value = user.name
        )

        KeyValueText(
            key = stringResource(R.string.admin_group_member_email),
            value = user.email
        )

        Spacer(modifier = Modifier.height(200.dp))

        Button(
            onClick = { goBack() },
            contentPadding = PaddingValues(horizontal = 40.dp, vertical = 5.dp),
            modifier = Modifier
                .size(width = 250.dp, height = 45.dp)
        ) {
            Text(
                text = stringResource(R.string.dismiss),
                color = Color.LightGray,
                fontSize = 26.sp
            )
        }

        Spacer(modifier = Modifier.width(40.dp))

    }  // Column

}


@Preview(
    name = "Product Details",
    showSystemUi = true,
)
@Composable
fun PreviewSmobItemDetailsProduct() {

    ShopMobTheme {
        AdminProfileDetailsView(
            SmobUserATO()
        )
    }

}
