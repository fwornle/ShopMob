package com.tanfra.shopmob.features.smobAdmin.presentation.view.browse

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanfra.shopmob.R
import com.tanfra.shopmob.features.common.theme.ShopMobTheme
import com.tanfra.shopmob.features.common.theme.secondaryLightColor

@Composable
fun AdminBrowseScreen(
    navigateToUserDetails: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(secondaryLightColor),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = CenterHorizontally
    ) {

        Spacer(modifier = Modifier.width(16.dp))
        Image(
            painter = painterResource(R.drawable.smob_1),
            contentDescription = stringResource(R.string.smob_icon),
            modifier = Modifier.size(204.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = { navigateToUserDetails() },
            contentPadding = PaddingValues(horizontal = 40.dp, vertical = 5.dp),
            modifier = Modifier.size(width = 250.dp, height = 45.dp)
            ) {
            Text(
                text = stringResource(R.string.profile),
                fontSize = 26.sp
            )
        }

        Button(
            onClick = {  },
            contentPadding = PaddingValues(horizontal = 40.dp, vertical = 5.dp),
            modifier = Modifier.size(width = 250.dp, height = 45.dp)
        ) {
            Text(
                text = stringResource(R.string.contacts),
                fontSize = 26.sp
            )
        }

        Button(
            onClick = {  },
            contentPadding = PaddingValues(horizontal = 40.dp, vertical = 5.dp),
            modifier = Modifier.size(width = 250.dp, height = 45.dp)
        ) {
            Text(
                text = stringResource(R.string.groups),
                fontSize = 26.sp
            )
        }

        Button(
            onClick = {  },
            contentPadding = PaddingValues(horizontal = 40.dp, vertical = 5.dp),
            modifier = Modifier.size(width = 250.dp, height = 45.dp)
        ) {
            Text(
                text = stringResource(R.string.lists),
                fontSize = 26.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = {  },
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

        Spacer(modifier = Modifier.width(16.dp))

    }
}


@Preview(
    name = "Light Mode",
    showSystemUi = true,
)
@Composable
fun PreviewAdminSelectorScreen() {
    ShopMobTheme {
        AdminBrowseScreen(
            navigateToUserDetails = {}
        )
    }
}
