package com.common.ui.screens

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.common.ui.R
import kotlinx.coroutines.delay

@Preview(showSystemUi = true)
@Composable
fun AskSplashScreen(
    modifier: Modifier = Modifier,
    execute: () -> Unit = {}
) {

    val scale = remember { androidx.compose.animation.core.Animatable(0f) }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 2000,
                delayMillis = 100,
                easing =  {
                    OvershootInterpolator(4f).getInterpolation(it)
                }
        )
        )
        delay(1000)
        execute()
    }


        Column(
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Row(
                modifier = modifier
                    .wrapContentSize()
                    .scale(scale.value)
                ,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ask_1),
                    contentDescription = "App Logo ",
                    modifier = modifier
                        .clip(CircleShape)
                        .padding(8.dp)
                        .size(120.dp)
                )
                Text(
                    text = "Ai Solution Kit",
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    fontStyle = FontStyle.Italic,
                )
            }
        }
    

}
