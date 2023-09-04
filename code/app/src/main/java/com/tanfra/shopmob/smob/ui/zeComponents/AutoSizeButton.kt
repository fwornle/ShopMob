package com.tanfra.shopmob.smob.ui.zeComponents

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tanfra.shopmob.smob.ui.zeTheme.ShopMobTheme

@Composable
fun AutoSizeButton(
    modifier: Modifier = Modifier,
) {

    val (isLoading, setIsLoading) = remember { mutableStateOf(false) }
    Button(onClick = { setIsLoading(!isLoading) }) {
        Text(
            text = if (isLoading) {
                "Short text"
            } else {
                "Very very very long text"
            },
            modifier = modifier.animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                        easing = LinearOutSlowInEasing
                )
            )
        )
    }

}

@Preview(
    name = "AutoSizeButton",
    showSystemUi = true,
)
@Composable
fun PreviewAutoSizeButton() {

    ShopMobTheme {
        AutoSizeButton()
    }

}