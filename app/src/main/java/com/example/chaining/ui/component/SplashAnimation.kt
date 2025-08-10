package com.example.chaining.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.chaining.R
import kotlinx.coroutines.launch

@Composable
fun SplashAnimation() {
    val offsetY = remember { Animatable(0f) } // 아래 -> 위

    LaunchedEffect(Unit) {
        launch {
            offsetY.animateTo(
                targetValue = -50f,
                animationSpec = tween(durationMillis = 200)
            )
        }
    }

    Image(
        painter = painterResource(id = R.drawable.chain),
        contentDescription = "Chain",
        modifier = Modifier
            .size(70.dp)
            .graphicsLayer {
                translationY = offsetY.value
            }
    )
}