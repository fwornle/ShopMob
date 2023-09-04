package com.tanfra.shopmob.smob.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.zeTheme.ShopMobTheme


@Composable
fun AuthScreen(login: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = CenterHorizontally
    ) {

        Spacer(modifier = Modifier.width(16.dp))
        Image(
            painter = painterResource(R.drawable.smob_2),
            contentDescription = stringResource(R.string.smob_icon),
            modifier = Modifier.size(204.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = stringResource(R.string.welcome_message),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = stringResource(R.string.signed_out),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.width(48.dp))

        Button(
            onClick = { login() },
            contentPadding = PaddingValues(horizontal = 80.dp, vertical = 5.dp),
            ) {
            Text(
                text = stringResource(R.string.login),
                fontSize = 26.sp
            )
        }

    }
}


@Preview(
    name = "Light Mode",
    showSystemUi = true,
)
@Composable
fun PreviewAuthScreen() {
    ShopMobTheme {
        AuthScreen({})
    }
}
