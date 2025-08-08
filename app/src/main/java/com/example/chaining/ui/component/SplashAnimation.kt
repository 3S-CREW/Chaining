package com.example.chaining.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.chaining.R
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween

@Composable
fun SplashAnimation() {
    var startAnimation by remember { mutableStateOf(false) }

    val chainWidth by animateDpAsState(
        targetValue = if (startAnimation) 200.dp else 0.dp,
        animationSpec = tween(durationMillis = 1000)
    )

    LaunchedEffect(true) {
        startAnimation = true
    }

    Image(
        painter = painterResource(id = R.drawable.chain),
        contentDescription = "Chain",
        modifier = Modifier
            .size(64.dp)
            .height(20.dp)
            .width(chainWidth)
    )
}
