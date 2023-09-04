package com.tanfra.shopmob.smob.ui.zeComponents

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanfra.shopmob.smob.ui.zeTheme.ShopMobTheme
import timber.log.Timber

@Composable
fun ColorFaderButton(
    modifier: Modifier = Modifier,
    label: String = "ColorFaderButton",
    onClick: () -> Unit = { Timber.i("ColorFaderButton clicked") },
) {

    // animation state (colors)
    val infiniteTransition = rememberInfiniteTransition(label = "color fader")
    val color by infiniteTransition.animateColor(
        initialValue = Color.Red,
        targetValue = Color.Green,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color fader animation"
    )

    Box(
        modifier = modifier.fillMaxWidth(),
    ) {

        // need an inner Box to control alignment - "inner" property (!?)
        Box(modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp)
            .background(color)
            .align(Alignment.Center)
            .clickable { onClick() }
        ) {

            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Text(
                    text = label,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )

            }

        }  // inner Box

    }  // outer Box

}

@Preview(
    name = "ColorFader",
    showSystemUi = true,
)
@Composable
fun PreviewColorFaderButton() {

    ShopMobTheme {
        ColorFaderButton(
            Modifier.height(100.dp),
            label = "hello world",
            { Timber.i("clicked") }
        )
    }

}