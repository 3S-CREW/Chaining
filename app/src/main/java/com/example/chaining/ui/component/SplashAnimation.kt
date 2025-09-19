package com.example.chaining.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.chaining.R
import kotlinx.coroutines.launch

@Suppress("FunctionName")
@Composable
fun SplashAnimation(startAnimation: Boolean) {
    // 아래 -> 위
    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(startAnimation) {
        if (startAnimation) {
            launch {
                offsetY.animateTo(
                    targetValue = -50f,
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                )
            }
            launch { // 투명도 애니메이션 추가
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 400),
                )
            }
        }
    }

    Image(
        painter = painterResource(id = R.drawable.chain),
        contentDescription = "Chain",
        modifier =
        Modifier
            .size(90.dp)
            .graphicsLayer {
                translationY = offsetY.value
                this.alpha = alpha.value
            },
    )
}
