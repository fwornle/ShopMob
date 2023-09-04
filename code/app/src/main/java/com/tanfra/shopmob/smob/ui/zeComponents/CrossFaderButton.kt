package com.tanfra.shopmob.smob.ui.zeComponents

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanfra.shopmob.smob.ui.zeTheme.ShopMobTheme
import timber.log.Timber

@Composable
fun CrossFaderButton(
    modifier: Modifier = Modifier,
    label: String = "CrossFaderButton",
    color1: Color = Color.LightGray,
    color2: Color = Color.DarkGray,
    onClick: () -> Unit = { Timber.i("CrossFaderButton clicked") },
    ) {

    // animation state
    val infiniteTransition = rememberInfiniteTransition(label = "cross fader")
    val perc by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cross fader animation"
    )

    Box(
        modifier = modifier.fillMaxWidth(),
    ) {

        Box(modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp)
            .background(Color.Gray)
            .align(Alignment.Center)
            .clickable { onClick() }
        ) {

            Canvas(
                modifier = modifier
                    .fillMaxSize()
            ) {

                // compute these only once
                val btnHeight = size.height
                val btnWidth = size.width
                val btnWidth1 = perc * btnWidth
                val btnWidth2 = (1.0f - perc) * btnWidth
                val topLeft2 = Offset(btnWidth1, 0f)


                // button anim phase #1
                drawRect(
                    color = color1,
                   size = Size(btnWidth1, btnHeight)
                )

                // button anim phase #2
                drawRect(
                    color = color2,
                    topLeft = topLeft2,
                    size = Size(btnWidth2, btnHeight)
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
    name = "CrossFader",
    showSystemUi = true,
)
@Composable
fun PreviewCrossFaderButton() {

    ShopMobTheme {
        CrossFaderButton(
            Modifier.height(100.dp),
            label = "hello world",
            color1 = Color.LightGray,
            color2 = Color.DarkGray,
            { Timber.i("clicked") }
        )
    }

}