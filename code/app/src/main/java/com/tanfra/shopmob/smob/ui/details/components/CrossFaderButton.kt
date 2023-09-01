package com.tanfra.shopmob.smob.ui.details.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanfra.shopmob.smob.ui.theme.ShopMobTheme

@Composable
fun CrossFaderButton(
    modifier: Modifier = Modifier,
    label: String = "CrossFaderButton"
) {

    // animation state
    val infiniteTransition = rememberInfiniteTransition()
    val color by infiniteTransition.animateColor(
        initialValue = Color.Red,
        targetValue = Color.Green,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier.fillMaxWidth(),
    ) {

        Box(modifier = modifier
            .fillMaxSize()
            .align(Alignment.Center)
        ) {

            Canvas(
                modifier = modifier.fillMaxSize()
            ) {

                val heightButton = size.height
                val widthButton = size.width * .8f
                val topLeft = Offset((size.width - widthButton) / 2, 0f)


                // button anim phase #1
                drawRect(
                    color = color,
                    topLeft = topLeft,
                    size = Size(widthButton, heightButton)
                )

            }  // Canvas

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
    name = "Custom View",
    showSystemUi = true,
)
@Composable
fun PreviewCrossFaderButton() {

    ShopMobTheme {
        CrossFaderButton(
            Modifier.height(100.dp),
            label = "hello world",
        )
    }

}