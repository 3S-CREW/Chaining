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
fun SplashAnimation(startAnimation: Boolean) {
    val offsetY = remember { Animatable(0f) } // 아래 -> 위
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(startAnimation) {
        if (startAnimation) {
            launch {
                offsetY.animateTo(
                    targetValue = -100f,
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                )
            }
            launch { // 투명도 애니메이션 추가
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 400)
                )
            }
        }
    }

    Image(
        painter = painterResource(id = R.drawable.chain),
        contentDescription = "Chain",
        modifier = Modifier
            .size(70.dp)
            .graphicsLayer {
                translationY = offsetY.value
                this.alpha = alpha.value
            }
    )
}