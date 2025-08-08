package com.example.chaining.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Text
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import androidx.compose.animation.core.FastOutSlowInEasing
import com.example.chaining.ui.component.SplashAnimation

@Composable
fun SplashScreen(navController: NavController) {
    val offsetX = remember { Animatable(300f) }
    val chainVisible = remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        offsetX.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
        delay(300)
        chainVisible.value = true
        delay(1500)
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (chainVisible.value) {
                SplashAnimation()
                Spacer(modifier = Modifier.height(8.dp)) // 이미지와 텍스트 간격
            }

            Text(
                text = "Chaining",
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.offset { IntOffset(offsetX.value.toInt(), 0) }
            )
        }

    }
}